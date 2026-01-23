package pl.gdansk.cinema.cinema_booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;
import pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UzytkownikRepository uzytkownikRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilmRepository filmRepository;
    private final SalaRepository salaRepository;
    private final SeansRepository seansRepository;

    @Override
    public void run(String... args) {
        if (uzytkownikRepository.count() == 0) {
            // Admin
            Uzytkownik admin = Uzytkownik.builder()
                    .username("admin")
                    .haslo(passwordEncoder.encode("admin"))
                    .role(Set.of("ADMIN", "USER"))
                    .build();
            uzytkownikRepository.save(admin);

            // Users
            for (int i = 1; i <= 3; i++) {
                Uzytkownik user = Uzytkownik.builder()
                        .username("user" + i)
                        .haslo(passwordEncoder.encode("user"))
                        .role(Set.of("USER"))
                        .build();
                uzytkownikRepository.save(user);
            }
            System.out.println(">>> Stworzono konta testowe: admin/admin, user1-3/user");
        }

        if (salaRepository.count() == 0) {
            Sala sala1 = Sala.builder().numer(1).rzedy(10).miejscaWRzedzie(15).opis("Sala Duża").build();
            Sala sala2 = Sala.builder().numer(2).rzedy(8).miejscaWRzedzie(12).opis("Sala Kameralna").build();
            Sala sala3 = Sala.builder().numer(3).rzedy(6).miejscaWRzedzie(10).opis("Sala VIP").build();
            salaRepository.saveAll(List.of(sala1, sala2, sala3));
            System.out.println(">>> Stworzono sale kinowe (3)");
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
                    .trailerYoutubeId("Xithigfg7dA")
                    .galeriaUrls(List.of(
                            "/images/gallery/deadpool/1.png",
                            "/images/gallery/deadpool/2.png",
                            "/images/gallery/deadpool/3.png",
                            "/images/gallery/deadpool/4.png",
                            "/images/gallery/deadpool/5.png"
                    ))
                    .build();

            Film film2 = Film.builder()
                    .tytul("Jak wytresować smoka")
                    .gatunek("Przygodowy/Fantasy")
                    .wiek(8)
                    .rezyser("Dean DeBlois")
                    .obsada("Mason Thames, Nico Parker")
                    .obrazUrl("/images/posters/how_to_train_your_dragon.png")
                    .czasTrwania(125)
                    .trailerYoutubeId("22w7z_lT6YM")
                    .galeriaUrls(List.of(
                            "/images/gallery/how_to_train_your_dragon/1.png",
                            "/images/gallery/how_to_train_your_dragon/2.png",
                            "/images/gallery/how_to_train_your_dragon/3.png",
                            "/images/gallery/how_to_train_your_dragon/4.png",
                            "/images/gallery/how_to_train_your_dragon/5.png"
                    ))
                    .build();

            Film film3 = Film.builder()
                    .tytul("Zwierzogród 2")
                    .gatunek("Familijny/Komedia")
                    .wiek(8)
                    .rezyser("Jared Bush, Byron Howard")
                    .obsada("Andy Samberg, Ginnifer Goodwin, Jason Bateman")
                    .obrazUrl("/images/posters/zootopia_2.png")
                    .czasTrwania(110)
                    .trailerYoutubeId("5AwtptT8X8k")
                    .galeriaUrls(List.of(
                            "/images/gallery/zootopia_2/1.png",
                            "/images/gallery/zootopia_2/2.png",
                            "/images/gallery/zootopia_2/3.png",
                            "/images/gallery/zootopia_2/4.png",
                            "/images/gallery/zootopia_2/5.png"
                    ))
                    .build();

            Film film4 = Film.builder()
                    .tytul("Avatar: Ogień i Popiół")
                    .gatunek("Sci-Fi/Przygodowy")
                    .wiek(12)
                    .rezyser("James Cameron")
                    .obsada("Sam Worthington, Zoe Saldana, Sigourney Weaver")
                    .obrazUrl("/images/posters/avatar_3.png")
                    .czasTrwania(197)
                    .trailerYoutubeId("nb_fFj_0rq8")
                    .galeriaUrls(List.of(
                            "/images/gallery/avatar_3/1.png",
                            "/images/gallery/avatar_3/2.png",
                            "/images/gallery/avatar_3/3.png",
                            "/images/gallery/avatar_3/4.png",
                            "/images/gallery/avatar_3/5.png"
                    ))
                    .build();

            Film film5 = Film.builder()
                    .tytul("John Wick 4")
                    .gatunek("Akcja/Kryminał")
                    .wiek(18)
                    .rezyser("Chad Stahelski")
                    .obsada("Keanu Reeves, Donnie Yen, Bill Skarsgård")
                    .obrazUrl("/images/posters/john_wick_4.png")
                    .czasTrwania(169)
                    .trailerYoutubeId("qEVUtrk8_B4")
                    .galeriaUrls(List.of(
                            "/images/gallery/john_wick_4/1.png",
                            "/images/gallery/john_wick_4/2.png",
                            "/images/gallery/john_wick_4/3.png",
                            "/images/gallery/john_wick_4/4.png",
                            "/images/gallery/john_wick_4/5.png"
                    ))
                    .build();

            Film film6 = Film.builder()
                    .tytul("Minecraft Movie")
                    .gatunek("Przygodowy/Familijny")
                    .wiek(7)
                    .rezyser("Jared Hess")
                    .obsada("Jack Black, Jason Momoa, Emma Myers")
                    .obrazUrl("/images/posters/minecraft_movie.png")
                    .czasTrwania(105)
                    .trailerYoutubeId("wJO_vIDZn-I")
                    .galeriaUrls(List.of(
                            "/images/gallery/minecraft_movie/1.png",
                            "/images/gallery/minecraft_movie/2.png",
                            "/images/gallery/minecraft_movie/3.png",
                            "/images/gallery/minecraft_movie/4.png",
                            "/images/gallery/minecraft_movie/5.png"
                    ))
                    .build();

            Film film7 = Film.builder()
                    .tytul("Krzyk 7")
                    .gatunek("Horror/Mystery")
                    .wiek(18)
                    .rezyser("Kevin Williamson")
                    .obsada("Neve Campbell, Courteney Cox")
                    .obrazUrl("/images/posters/scream_7.png")
                    .czasTrwania(115)
                    .trailerYoutubeId("UJrghaPJ0RY")
                    .galeriaUrls(List.of(
                            "/images/gallery/scream_7/1.png",
                            "/images/gallery/scream_7/2.png",
                            "/images/gallery/scream_7/3.png",
                            "/images/gallery/scream_7/4.png",
                            "/images/gallery/scream_7/5.png"
                    ))
                    .build();

            filmRepository.saveAll(List.of(film1, film2, film3, film4, film5, film6, film7));
            System.out.println(">>> Stworzono pełny repertuar filmów (7 pozycji)");

            // Dodanie seansów
            List<Sala> sale = salaRepository.findAll();
            List<Film> filmy = filmRepository.findAll();
            if (sale.size() >= 3 && filmy.size() >= 7) {
                java.util.List<Seans> seanseToSave = new java.util.ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    LocalDateTime day = LocalDateTime.now().plusDays(i).withHour(10).withMinute(0).withSecond(0).withNano(0);

                    // Sala 1
                    seanseToSave.add(Seans.builder().film(filmy.get(0)).sala(sale.get(0)).dataGodzina(day.plusHours(0)).cenaNormalny(25.0).cenaUlgowy(20.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(3)).sala(sale.get(0)).dataGodzina(day.plusHours(4)).cenaNormalny(28.0).cenaUlgowy(22.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(5)).sala(sale.get(0)).dataGodzina(day.plusHours(8)).cenaNormalny(20.0).cenaUlgowy(15.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(6)).sala(sale.get(0)).dataGodzina(day.plusHours(12)).cenaNormalny(25.0).cenaUlgowy(20.0).build());

                    // Sala 2
                    seanseToSave.add(Seans.builder().film(filmy.get(1)).sala(sale.get(1)).dataGodzina(day.plusHours(1)).cenaNormalny(22.0).cenaUlgowy(18.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(2)).sala(sale.get(1)).dataGodzina(day.plusHours(5)).cenaNormalny(20.0).cenaUlgowy(15.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(4)).sala(sale.get(1)).dataGodzina(day.plusHours(9)).cenaNormalny(25.0).cenaUlgowy(20.0).build());

                    // Sala 3
                    seanseToSave.add(Seans.builder().film(filmy.get(0)).sala(sale.get(2)).dataGodzina(day.plusHours(2)).cenaNormalny(30.0).cenaUlgowy(25.0).build());
                    seanseToSave.add(Seans.builder().film(filmy.get(3)).sala(sale.get(2)).dataGodzina(day.plusHours(6)).cenaNormalny(35.0).cenaUlgowy(30.0).build());
                }
                seansRepository.saveAll(seanseToSave);
                System.out.println(">>> Stworzono repertuar seansów na 7 dni");
            }
        }
    }
}