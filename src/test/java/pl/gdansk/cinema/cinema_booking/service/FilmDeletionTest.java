package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class FilmDeletionTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SeansRepository seansRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private pl.gdansk.cinema.cinema_booking.repository.BiletRepository biletRepository;

    @Autowired
    private pl.gdansk.cinema.cinema_booking.service.SeansService seansService;

    @Test
    void shouldSuccessfullyDeleteFilmWithSeans() {
        // Given
        Film film = Film.builder()
                .tytul("Film do usunięcia")
                .gatunek("Dramat")
                .build();
        film = filmRepository.save(film);

        Sala sala = Sala.builder().numer(1).rzedy(10).miejscaWRzedzie(10).build();
        sala = salaRepository.save(sala);

        Seans seans = Seans.builder()
                .film(film)
                .sala(sala)
                .dataGodzina(LocalDateTime.now().plusDays(1))
                .build();
        seansRepository.save(seans);

        final Long filmId = film.getId();
        final Long seansId = seans.getId();

        // When
        filmService.deleteFilm(filmId);
        filmRepository.flush();

        // Then
        assertThat(filmRepository.findById(filmId)).isEmpty();
        assertThat(seansRepository.findById(seansId)).isEmpty();
    }

    @Test
    void shouldSuccessfullyDeleteSeansWithTickets() {
        // Given
        Film film = filmRepository.save(Film.builder().tytul("Film").build());
        Sala sala = salaRepository.save(Sala.builder().numer(2).rzedy(5).miejscaWRzedzie(5).build());
        Seans seans = seansRepository.save(Seans.builder()
                .film(film)
                .sala(sala)
                .dataGodzina(LocalDateTime.now().plusDays(2))
                .build());

        pl.gdansk.cinema.cinema_booking.entity.Bilet bilet = biletRepository.save(pl.gdansk.cinema.cinema_booking.entity.Bilet.builder()
                .seans(seans)
                .rzad(1)
                .miejsce(1)
                .build());

        final Long seansId = seans.getId();
        final Long biletId = bilet.getId();

        // When
        seansService.deleteSeans(seansId);
        seansRepository.flush();

        // Then
        assertThat(seansRepository.findById(seansId)).isEmpty();
        assertThat(biletRepository.findById(biletId)).isEmpty();
    }
}
