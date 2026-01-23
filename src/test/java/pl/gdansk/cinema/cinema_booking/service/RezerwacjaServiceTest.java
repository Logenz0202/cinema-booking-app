package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gdansk.cinema.cinema_booking.dto.BiletDto;
import pl.gdansk.cinema.cinema_booking.entity.*;
import pl.gdansk.cinema.cinema_booking.repository.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RezerwacjaServiceTest {

    @Mock
    private BiletRepository biletRepository;
    @Mock
    private RezerwacjaRepository rezerwacjaRepository;
    @Mock
    private SeansRepository seansRepository;
    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @InjectMocks
    private RezerwacjaService rezerwacjaService;

    @Test
    void shouldFinalizeReservation() {
        // Given
        Long seansId = 1L;
        String username = "user";
        Uzytkownik user = Uzytkownik.builder().username(username).build();
        Seans seans = Seans.builder().id(seansId).build();
        List<BiletDto> bilety = List.of(BiletDto.builder().rzad(1).miejsce(1).typBiletu("NORMALNY").cena(25.0).build());

        when(uzytkownikRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(seansRepository.findById(seansId)).thenReturn(Optional.of(seans));
        when(biletRepository.existsBySeansIdAndRzadAndMiejsce(any(), any(), any())).thenReturn(false);
        when(rezerwacjaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String reservationNum = rezerwacjaService.finalizujRezerwacje(seansId, bilety, username);

        // Then
        assertThat(reservationNum).startsWith("TICK-");
        verify(rezerwacjaRepository).save(any());
        verify(biletRepository).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenSeatOccupied() {
        // Given
        Long seansId = 1L;
        String username = "user";
        Uzytkownik user = Uzytkownik.builder().username(username).build();
        Seans seans = Seans.builder().id(seansId).build();
        List<BiletDto> bilety = List.of(BiletDto.builder().rzad(1).miejsce(1).typBiletu("NORMALNY").cena(25.0).build());

        when(uzytkownikRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(seansRepository.findById(seansId)).thenReturn(Optional.of(seans));
        when(biletRepository.existsBySeansIdAndRzadAndMiejsce(eq(seansId), eq(1), eq(1))).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> rezerwacjaService.finalizujRezerwacje(seansId, bilety, username));
    }
}
