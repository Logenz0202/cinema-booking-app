package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.exception.ResourceNotFoundException;
import pl.gdansk.cinema.cinema_booking.mapper.FilmMapper;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.SeansRepository seansRepository;
    private final FilmMapper filmMapper;

    @Transactional(readOnly = true)
    public List<FilmDto> getAllFilmy() {
        log.debug("Pobieranie wszystkich filmów");
        return filmRepository.findAll().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FilmDto> getAllFilmy(Pageable pageable) {
        log.debug("Pobieranie stronicowanych filmów: {}", pageable);
        return filmRepository.findAll(pageable)
                .map(filmMapper::toDto);
    }

    @Transactional(readOnly = true)
    public FilmDto getFilmById(Long id) {
        log.debug("Pobieranie filmu o ID: {}", id);
        return filmRepository.findById(id)
                .map(filmMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono filmu o ID: {}", id);
                    return new ResourceNotFoundException("Film nie znaleziony o id: " + id);
                });
    }

    @Transactional
    public FilmDto createFilm(FilmDto filmDto) {
        log.info("Tworzenie nowego filmu: {}", filmDto.getTytul());
        Film film = filmMapper.toEntity(filmDto);
        Film savedFilm = filmRepository.save(film);
        log.info("Film utworzony pomyślnie z ID: {}", savedFilm.getId());
        return filmMapper.toDto(savedFilm);
    }

    @Transactional
    public FilmDto updateFilm(Long id, FilmDto filmDto) {
        log.info("Aktualizacja filmu o ID: {}", id);
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono filmu o ID: {} podczas aktualizacji", id);
                    return new ResourceNotFoundException("Film nie znaleziony o id: " + id);
                });
        
        existingFilm.setTytul(filmDto.getTytul());
        existingFilm.setGatunek(filmDto.getGatunek());
        existingFilm.setWiek(filmDto.getWiek());
        existingFilm.setRezyser(filmDto.getRezyser());
        existingFilm.setObsada(filmDto.getObsada());
        existingFilm.setOpis(filmDto.getOpis());
        existingFilm.setCzasTrwania(filmDto.getCzasTrwania());
        existingFilm.setObrazUrl(filmDto.getObrazUrl());
        existingFilm.setTrailerYoutubeId(filmDto.getTrailerYoutubeId());
        existingFilm.setGaleriaUrls(filmDto.getGaleriaUrls());
        
        Film updatedFilm = filmRepository.save(existingFilm);
        log.info("Film o ID: {} został zaktualizowany", id);
        return filmMapper.toDto(updatedFilm);
    }

    @Transactional
    public void updateFilmPoster(Long id, String imageUrl) {
        log.info("Aktualizacja plakatu dla filmu o ID: {}", id);
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film nie znaleziony o id: " + id));
        existingFilm.setObrazUrl(imageUrl);
        filmRepository.save(existingFilm);
        log.debug("Nowy adres plakatu: {}", imageUrl);
    }

    @Transactional
    public void deleteFilm(Long id) {
        log.info("Usuwanie filmu o ID: {}", id);
        if (!filmRepository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącego filmu o ID: {}", id);
            throw new ResourceNotFoundException("Film nie znaleziony o id: " + id);
        }
        // Najpierw usuwamy wszystkie seanse powiązane z filmem
        // Kaskada w Seans zajmie się biletami i rezerwacjami
        List<pl.gdansk.cinema.cinema_booking.entity.Seans> seanse = seansRepository.findByFilmId(id);
        log.debug("Usuwanie {} seansów powiązanych z filmem ID: {}", seanse.size(), id);
        seansRepository.deleteAll(seanse);
        
        filmRepository.deleteById(id);
        log.info("Film o ID: {} został usunięty", id);
    }
}
