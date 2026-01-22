package pl.gdansk.cinema.cinema_booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.util.List;
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
                    .obrazUrl("/images/posters/deadpool.png")
                    .czasTrwania(108)
                    .build();

            Film film2 = Film.builder()
                    .tytul("Jak wytresować smoka")
                    .gatunek("Przygodowy/Fantasy")
                    .wiek(8)
                    .rezyser("Dean DeBlois")
                    .obsada("Mason Thames, Nico Parker")
                    .obrazUrl("/images/posters/httyd.png")
                    .czasTrwania(125)
                    .build();

            Film film3 = Film.builder()
                    .tytul("Zwierzogród 2")
                    .gatunek("Familijny/Komedia")
                    .wiek(8)
                    .rezyser("Jared Bush, Byron Howard")
                    .obsada("Andy Samberg, Ginnifer Goodwin, Jason Bateman")
                    .obrazUrl("/images/posters/zootopia_2.png")
                    .czasTrwania(110)
                    .build();

            Film film4 = Film.builder()
                    .tytul("Avatar: Ogień i Popiół")
                    .gatunek("Sci-Fi/Przygodowy")
                    .wiek(12)
                    .rezyser("James Cameron")
                    .obsada("Sam Worthington, Zoe Saldana, Sigourney Weaver")
                    .obrazUrl("/images/posters/avatar_3.png")
                    .czasTrwania(197)
                    .build();

            Film film5 = Film.builder()
                    .tytul("John Wick 4")
                    .gatunek("Akcja/Kryminał")
                    .wiek(18)
                    .rezyser("Chad Stahelski")
                    .obsada("Keanu Reeves, Donnie Yen, Bill Skarsgård")
                    .obrazUrl("/images/posters/john_wick_4.png")
                    .czasTrwania(169)
                    .build();

            Film film6 = Film.builder()
                    .tytul("Minecraft Movie")
                    .gatunek("Przygodowy/Familijny")
                    .wiek(7)
                    .rezyser("Jared Hess")
                    .obsada("Jack Black, Jason Momoa, Emma Myers")
                    .obrazUrl("/images/posters/minecraft_movie.png")
                    .czasTrwania(105)
                    .build();

            Film film7 = Film.builder()
                    .tytul("Krzyk 7")
                    .gatunek("Horror/Mystery")
                    .wiek(18)
                    .rezyser("Kevin Williamson")
                    .obsada("Neve Campbell, Courteney Cox")
                    .obrazUrl("/images/posters/scream_7.png")
                    .czasTrwania(115)
                    .build();

            filmRepository.saveAll(List.of(film1, film2, film3, film4, film5, film6, film7));
            System.out.println(">>> Stworzono pełny repertuar filmów (7 pozycji)");
        }
    }
}