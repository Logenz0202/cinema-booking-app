package pl.gdansk.cinema.cinema_booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilmRepository filmRepository;

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

        if (filmRepository.count() == 0) {
            Film film1 = Film.builder()
                    .tytul("Deadpool")
                    .gatunek("Akcja/Komedia")
                    .wiek(18)
                    .rezyser("Tim Miller")
                    .obsada("Ryan Reynolds, Morena Baccarin")
                    .obrazUrl("/images/posters/deadpool.jpg")
                    .build();

            Film film2 = Film.builder()
                    .tytul("Jak wytresować smoka")
                    .gatunek("Przygodowy/Fantasy")
                    .wiek(8)
                    .rezyser("Dean DeBlois")
                    .obsada("Mason Thames, Nico Parker")
                    .obrazUrl("/images/posters/httyd.png")
                    .build();

            Film film3 = Film.builder()
                    .tytul("Zwierzogród 2")
                    .gatunek("Familijny/Komedia")
                    .wiek(8)
                    .rezyser("Jared Bush, Byron Howard")
                    .obsada("Andy Samberg, Ginnifer Goodwin, Jason Bateman")
                    .obrazUrl("/images/posters/zootopia_2.jpg")
                    .build();

            filmRepository.save(film1);
            filmRepository.save(film2);
            filmRepository.save(film3);
            System.out.println(">>> Stworzono przykładowe filmy z obrazkami");
        }
    }
}