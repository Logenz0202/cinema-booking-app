package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.exception.ResourceNotFoundException;
import pl.gdansk.cinema.cinema_booking.mapper.FilmMapper;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private SeansRepository seansRepository;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    @Test
    void shouldGetFilmById() {
        Film film = Film.builder().id(1L).tytul("Test").build();
        FilmDto dto = FilmDto.builder().id(1L).tytul("Test").build();
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmMapper.toDto(film)).thenReturn(dto);

        FilmDto result = filmService.getFilmById(1L);

        assertThat(result.getTytul()).isEqualTo("Test");
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFound() {
        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> filmService.getFilmById(1L));
    }

    @Test
    void shouldCreateFilm() {
        Film film = Film.builder().id(1L).tytul("Test").build();
        FilmDto dto = FilmDto.builder().tytul("Test").build();
        when(filmMapper.toEntity(dto)).thenReturn(film);
        when(filmRepository.save(film)).thenReturn(film);
        when(filmMapper.toDto(film)).thenReturn(FilmDto.builder().id(1L).tytul("Test").build());

        FilmDto result = filmService.createFilm(dto);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder().id(1L).tytul("Old").build();
        FilmDto dto = FilmDto.builder().tytul("New").build();
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(filmMapper.toDto(any())).thenReturn(dto);

        FilmDto result = filmService.updateFilm(1L, dto);

        assertThat(result.getTytul()).isEqualTo("New");
    }

    @Test
    void shouldUpdateFilmPoster() {
        Film film = Film.builder().id(1L).tytul("Test").obrazUrl("old.png").build();
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));

        filmService.updateFilmPoster(1L, "new.png");

        assertThat(film.getObrazUrl()).isEqualTo("new.png");
        verify(filmRepository).save(film);
    }

    @Test
    void shouldDeleteFilm() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(seansRepository.findByFilmId(1L)).thenReturn(java.util.List.of());

        filmService.deleteFilm(1L);

        verify(filmRepository).deleteById(1L);
    }
}
