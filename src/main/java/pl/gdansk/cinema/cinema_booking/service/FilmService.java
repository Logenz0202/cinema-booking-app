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
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;

    @Transactional(readOnly = true)
    public List<FilmDto> getAllFilmy() {
        return filmRepository.findAll().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FilmDto> getAllFilmy(Pageable pageable) {
        return filmRepository.findAll(pageable)
                .map(filmMapper::toDto);
    }

    @Transactional(readOnly = true)
    public FilmDto getFilmById(Long id) {
        return filmRepository.findById(id)
                .map(filmMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Film nie znaleziony o id: " + id));
    }

    @Transactional
    public FilmDto createFilm(FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        Film savedFilm = filmRepository.save(film);
        return filmMapper.toDto(savedFilm);
    }

    @Transactional
    public FilmDto updateFilm(Long id, FilmDto filmDto) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film nie znaleziony o id: " + id));
        
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
        return filmMapper.toDto(updatedFilm);
    }

    @Transactional
    public void updateFilmPoster(Long id, String imageUrl) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film nie znaleziony o id: " + id));
        existingFilm.setObrazUrl(imageUrl);
        filmRepository.save(existingFilm);
    }

    @Transactional
    public void deleteFilm(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new ResourceNotFoundException("Film nie znaleziony o id: " + id);
        }
        filmRepository.deleteById(id);
    }
}
