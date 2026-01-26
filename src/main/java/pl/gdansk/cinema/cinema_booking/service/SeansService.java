package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.exception.ResourceNotFoundException;
import pl.gdansk.cinema.cinema_booking.mapper.SeansMapper;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeansService {
    private final SeansRepository seansRepository;
    private final FilmRepository filmRepository;
    private final SalaRepository salaRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.BiletRepository biletRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.RezerwacjaRepository rezerwacjaRepository;
    private final SeansMapper seansMapper;

    private static final int CLEANING_TIME_MINUTES = 15;

    @Transactional(readOnly = true)
    public List<SeansDto> getAllSeanse() {
        return seansRepository.findAll().stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SeansDto> getSeansePaged(Pageable pageable) {
        return seansRepository.findAll(pageable)
                .map(seansMapper::toDto);
    }

    @Transactional(readOnly = true)
    public SeansDto getSeansById(Long id) {
        return seansRepository.findById(id)
                .map(seansMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono seansu o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByFilmId(Long filmId) {
        return seansRepository.findByFilmIdAndDataGodzinaAfterOrderByDataGodzinaAsc(filmId, LocalDateTime.now().toLocalDate().atStartOfDay()).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByFilmIdAndDate(Long filmId, LocalDateTime startOfDay) {
        LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59);
        return seansRepository.findByFilmIdAndDataGodzinaBetween(filmId, startOfDay, endOfDay).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByDate(LocalDateTime startOfDay) {
        LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59);
        return seansRepository.findByDataGodzinaBetween(startOfDay, endOfDay).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeansDto createSeans(SeansDto seansDto) {
        validateSeans(seansDto);
        
        Film film = filmRepository.findById(seansDto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono filmu o ID: " + seansDto.getFilmId()));
        Sala sala = salaRepository.findById(seansDto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + seansDto.getSalaId()));

        Seans seans = seansMapper.toEntity(seansDto);
        seans.setFilm(film);
        seans.setSala(sala);

        return seansMapper.toDto(seansRepository.save(seans));
    }

    @Transactional
    public SeansDto updateSeans(Long id, SeansDto seansDto) {
        Seans existingSeans = seansRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono seansu o ID: " + id));
        
        seansDto.setId(id);
        validateSeans(seansDto);

        Film film = filmRepository.findById(seansDto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono filmu o ID: " + seansDto.getFilmId()));
        Sala sala = salaRepository.findById(seansDto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + seansDto.getSalaId()));

        existingSeans.setFilm(film);
        existingSeans.setSala(sala);
        existingSeans.setDataGodzina(seansDto.getDataGodzina());
        existingSeans.setCenaNormalny(seansDto.getCenaNormalny());
        existingSeans.setCenaUlgowy(seansDto.getCenaUlgowy());
        existingSeans.setCenaRodzinny(seansDto.getCenaRodzinny());

        return seansMapper.toDto(seansRepository.save(existingSeans));
    }

    @Transactional
    public void deleteSeans(Long id) {
        if (!seansRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono seansu o ID: " + id);
        }
        // Najpierw bilety i rezerwacje
        biletRepository.deleteAll(biletRepository.findBySeansId(id));
        rezerwacjaRepository.deleteAll(rezerwacjaRepository.findBySeansId(id));
        
        seansRepository.deleteById(id);
    }

    private void validateSeans(SeansDto seansDto) {
        Film film = filmRepository.findById(seansDto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono filmu o ID: " + seansDto.getFilmId()));
        
        LocalDateTime start = seansDto.getDataGodzina();
        LocalDateTime end = start.plusMinutes(film.getCzasTrwania() + CLEANING_TIME_MINUTES);

        // Pobierz wszystkie seanse w tej sali w tym samym dniu (z zapasem ± kilka godzin dla pewności)
        List<Seans> showsInSala = seansRepository.findBySalaIdAndDataGodzinaBetween(
                seansDto.getSalaId(),
                start.toLocalDate().atStartOfDay(),
                start.toLocalDate().atTime(23, 59)
        );

        for (Seans s : showsInSala) {
            if (seansDto.getId() != null && seansDto.getId().equals(s.getId())) {
                continue;
            }
            
            LocalDateTime sStart = s.getDataGodzina();
            LocalDateTime sEnd = sStart.plusMinutes(s.getFilm().getCzasTrwania() + CLEANING_TIME_MINUTES);

            if (start.isBefore(sEnd) && end.isAfter(sStart)) {
                throw new IllegalStateException("Seans nakłada się na inny seans w tej sali (" 
                        + s.getFilm().getTytul() + " " + sStart.toLocalTime() + "-" + sEnd.toLocalTime() + ")");
            }
        }
    }
}
