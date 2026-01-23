package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        Uzytkownik user = Uzytkownik.builder()
                .username("test")
                .haslo("pass")
                .role(Set.of("USER"))
                .build();
        when(uzytkownikRepository.findByUsername("test")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("test");

        assertThat(result.getUsername()).isEqualTo("test");
        assertThat(result.getAuthorities()).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(uzytkownikRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
