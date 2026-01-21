package pl.gdansk.cinema.cinema_booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (uzytkownikRepository.findByEmail("admin@kino.pl").isEmpty()) {
            Uzytkownik admin = Uzytkownik.builder()
                    .email("admin@kino.pl")
                    .haslo(passwordEncoder.encode("admin"))
                    .role(Set.of("ADMIN", "USER"))
                    .build();

            uzytkownikRepository.save(admin);
            System.out.println(">>> Stworzono domyślnego użytkownika: admin@kino.pl / admin");
        }
    }
}