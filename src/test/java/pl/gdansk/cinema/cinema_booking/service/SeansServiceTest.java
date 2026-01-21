package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.mapper.SeansMapper;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeansServiceTest {

    @Mock
    private SeansRepository seansRepository;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private SalaRepository salaRepository;
    @Mock
    private SeansMapper seansMapper;

    @InjectMocks
    private SeansService seansService;

    private Film film;
    private Sala sala;

    @BeforeEach
    void setUp() {
        film = Film.builder().id(1L).tytul("Test Film").czasTrwania(100).build();
        sala = Sala.builder().id(1L).numer(1).build();
    }

    @Test
    void shouldThrowExceptionWhenSeansOverlaps() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        SeansDto newSeansDto = SeansDto.builder()
                .filmId(1L)
                .salaId(1L)
                .dataGodzina(start)
                .build();

        Seans existingSeans = Seans.builder()
                .id(2L)
                .film(film)
                .sala(sala)
                .dataGodzina(start.minusMinutes(30))
                .build();

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(seansRepository.findBySalaIdAndDataGodzinaBetween(any(), any(), any()))
                .thenReturn(List.of(existingSeans));

        // When & Then
        assertThrows(IllegalStateException.class, () -> seansService.createSeans(newSeansDto));
    }
}
