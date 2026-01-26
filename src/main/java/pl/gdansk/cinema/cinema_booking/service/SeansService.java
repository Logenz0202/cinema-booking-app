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
@lombok.extern.slf4j.Slf4j
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
        log.debug("Pobieranie wszystkich seansów");
        return seansRepository.findAll().stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SeansDto> getSeansePaged(Pageable pageable) {
        log.debug("Pobieranie stronicowanych seansów: {}", pageable);
        return seansRepository.findAll(pageable)
                .map(seansMapper::toDto);
    }

    @Transactional(readOnly = true)
    public SeansDto getSeansById(Long id) {
        log.debug("Pobieranie seansu o ID: {}", id);
        return seansRepository.findById(id)
                .map(seansMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono seansu o ID: {}", id);
                    return new ResourceNotFoundException("Nie znaleziono seansu o ID: " + id);
                });
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByFilmId(Long filmId) {
        log.debug("Pobieranie nadchodzących seansów dla filmu o ID: {}", filmId);
        return seansRepository.findByFilmIdAndDataGodzinaAfterOrderByDataGodzinaAsc(filmId, LocalDateTime.now().toLocalDate().atStartOfDay()).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByFilmIdAndDate(Long filmId, LocalDateTime startOfDay) {
        log.debug("Pobieranie seansów dla filmu {} w dniu {}", filmId, startOfDay.toLocalDate());
        LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59);
        return seansRepository.findByFilmIdAndDataGodzinaBetween(filmId, startOfDay, endOfDay).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeansDto> getSeanseByDate(LocalDateTime startOfDay) {
        log.debug("Pobieranie wszystkich seansów w dniu {}", startOfDay.toLocalDate());
        LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59);
        return seansRepository.findByDataGodzinaBetween(startOfDay, endOfDay).stream()
                .map(seansMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeansDto createSeans(SeansDto seansDto) {
        log.info("Tworzenie nowego seansu dla filmu ID: {}, sala ID: {}, data: {}", 
                seansDto.getFilmId(), seansDto.getSalaId(), seansDto.getDataGodzina());
        validateSeans(seansDto);
        
        Film film = filmRepository.findById(seansDto.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono filmu o ID: " + seansDto.getFilmId()));
        Sala sala = salaRepository.findById(seansDto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + seansDto.getSalaId()));

        Seans seans = seansMapper.toEntity(seansDto);
        seans.setFilm(film);
        seans.setSala(sala);

        Seans saved = seansRepository.save(seans);
        log.info("Seans utworzony pomyślnie z ID: {}", saved.getId());
        return seansMapper.toDto(saved);
    }

    @Transactional
    public SeansDto updateSeans(Long id, SeansDto seansDto) {
        log.info("Aktualizacja seansu o ID: {}", id);
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

        Seans updated = seansRepository.save(existingSeans);
        log.info("Seans o ID: {} został zaktualizowany", id);
        return seansMapper.toDto(updated);
    }

    @Transactional
    public void deleteSeans(Long id) {
        log.info("Usuwanie seansu o ID: {}", id);
        if (!seansRepository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącego seansu o ID: {}", id);
            throw new ResourceNotFoundException("Nie znaleziono seansu o ID: " + id);
        }
        // Najpierw bilety i rezerwacje
        biletRepository.deleteAll(biletRepository.findBySeansId(id));
        rezerwacjaRepository.deleteAll(rezerwacjaRepository.findBySeansId(id));
        
        seansRepository.deleteById(id);
        log.info("Seans o ID: {} został usunięty wraz z powiązanymi biletami i rezerwacjami", id);
    }

    private void validateSeans(SeansDto seansDto) {
        log.debug("Walidacja nakładania się seansów dla sali ID: {} i godziny: {}", seansDto.getSalaId(), seansDto.getDataGodzina());
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
                String errorMsg = "Seans nakłada się na inny seans w tej sali (" 
                        + s.getFilm().getTytul() + " " + sStart.toLocalTime() + "-" + sEnd.toLocalTime() + ")";
                log.warn("Walidacja nieudana: {}", errorMsg);
                throw new IllegalStateException(errorMsg);
            }
        }
    }
}
