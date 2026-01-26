package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.gdansk.cinema.cinema_booking.dto.RejestracjaDto;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UzytkownikService {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;

    public void zarejestruj(RejestracjaDto dto) {
        log.info("Próba rejestracji nowego użytkownika: {}", dto.getUsername());
        if (uzytkownikRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.warn("Rejestracja nieudana: użytkownik {} już istnieje", dto.getUsername());
            throw new RuntimeException("Użytkownik o podanej nazwie już istnieje");
        }

        Uzytkownik uzytkownik = Uzytkownik.builder()
                .username(dto.getUsername())
                .haslo(passwordEncoder.encode(dto.getHaslo()))
                .role(Set.of("USER"))
                // Aby dodać więcej parametrów w przyszłości (np. email):
                // .email(dto.getEmail())
                .build();

        uzytkownikRepository.save(uzytkownik);
        log.info("Użytkownik {} został pomyślnie zarejestrowany", dto.getUsername());
    }
}
