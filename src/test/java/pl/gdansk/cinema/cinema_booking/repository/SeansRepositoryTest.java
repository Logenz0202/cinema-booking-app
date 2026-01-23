package pl.gdansk.cinema.cinema_booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.entity.Seans;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeansRepositoryTest {

    @Autowired
    private SeansRepository seansRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Test
    void shouldFindByFilmId() {
        Film film = filmRepository.save(Film.builder().tytul("Test Film").build());
        seansRepository.save(Seans.builder().film(film).dataGodzina(LocalDateTime.now()).build());

        List<Seans> seansy = seansRepository.findByFilmId(film.getId());

        assertThat(seansy).hasSize(1);
        assertThat(seansy.get(0).getFilm().getTytul()).isEqualTo("Test Film");
    }

    @Test
    void shouldFindByFilmIdAndDataGodzinaAfter() {
        Film film = filmRepository.save(Film.builder().tytul("Test Film").build());
        LocalDateTime now = LocalDateTime.now();
        seansRepository.save(Seans.builder().film(film).dataGodzina(now.minusHours(1)).build());
        seansRepository.save(Seans.builder().film(film).dataGodzina(now.plusHours(1)).build());

        List<Seans> seansy = seansRepository.findByFilmIdAndDataGodzinaAfterOrderByDataGodzinaAsc(film.getId(), now);

        assertThat(seansy).hasSize(1);
        assertThat(seansy.get(0).getDataGodzina()).isAfter(now);
    }

    @Test
    void shouldFindBySalaIdAndDataGodzinaBetween() {
        Sala sala = salaRepository.save(Sala.builder().numer(1).build());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(5);
        
        seansRepository.save(Seans.builder().sala(sala).dataGodzina(start.plusHours(2)).build());
        seansRepository.save(Seans.builder().sala(sala).dataGodzina(start.plusHours(6)).build());

        List<Seans> seansy = seansRepository.findBySalaIdAndDataGodzinaBetween(sala.getId(), start, end);

        assertThat(seansy).hasSize(1);
    }

    @Test
    void shouldFindByDataGodzinaBetween() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(5);

        seansRepository.save(Seans.builder().dataGodzina(start.plusHours(1)).build());
        seansRepository.save(Seans.builder().dataGodzina(start.plusHours(6)).build());

        List<Seans> seansy = seansRepository.findByDataGodzinaBetween(start, end);

        assertThat(seansy).hasSize(1);
    }
}
