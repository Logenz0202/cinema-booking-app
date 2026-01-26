package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.gdansk.cinema.cinema_booking.dto.RejestracjaDto;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UzytkownikServiceTest {

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UzytkownikService uzytkownikService;

    private RejestracjaDto dto;

    @BeforeEach
    void setUp() {
        dto = RejestracjaDto.builder()
                .username("nowyUser")
                .haslo("haslo123")
                .build();
    }

    @Test
    void zarejestruj_PowinnoZapisacUzytkownika_GdyDaneSaPoprawne() {
        // given
        when(uzytkownikRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getHaslo())).thenReturn("encodedHaslo");

        // when
        uzytkownikService.zarejestruj(dto);

        // then
        verify(uzytkownikRepository, times(1)).save(any(Uzytkownik.class));
    }

    @Test
    void zarejestruj_PowinnoRzucicWyjatek_GdyUzytkownikJuzIstnieje() {
        // given
        when(uzytkownikRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new Uzytkownik()));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> uzytkownikService.zarejestruj(dto));
        assertEquals("Użytkownik o podanej nazwie już istnieje", exception.getMessage());
        verify(uzytkownikRepository, never()).save(any(Uzytkownik.class));
    }
}
