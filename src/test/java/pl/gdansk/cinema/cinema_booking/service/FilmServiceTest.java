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
    void shouldDeleteFilm() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(seansRepository.findByFilmId(1L)).thenReturn(java.util.List.of());

        filmService.deleteFilm(1L);

        verify(filmRepository).deleteById(1L);
    }
}
