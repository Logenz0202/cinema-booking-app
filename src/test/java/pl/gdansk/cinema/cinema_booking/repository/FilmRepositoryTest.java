package pl.gdansk.cinema.cinema_booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.gdansk.cinema.cinema_booking.entity.Film;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FilmRepositoryTest {

    @Autowired
    private FilmRepository filmRepository;

    @Test
    void shouldSaveFilm() {
        Film film = Film.builder()
                .tytul("Inception")
                .gatunek("Sci-Fi")
                .wiek(12)
                .build();

        Film savedFilm = filmRepository.save(film);

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getTytul()).isEqualTo("Inception");
    }

    @Test
    void shouldFindById() {
        Film film = filmRepository.save(Film.builder().tytul("Matrix").build());
        Optional<Film> found = filmRepository.findById(film.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTytul()).isEqualTo("Matrix");
    }

    @Test
    void shouldFindAll() {
        filmRepository.save(Film.builder().tytul("F1").build());
        filmRepository.save(Film.builder().tytul("F2").build());
        List<Film> all = filmRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldDeleteFilm() {
        Film film = filmRepository.save(Film.builder().tytul("DeleteMe").build());
        filmRepository.deleteById(film.getId());
        assertThat(filmRepository.findById(film.getId())).isEmpty();
    }

    @Test
    void shouldFindByGatunek() {
        filmRepository.save(Film.builder().tytul("Dramat1").gatunek("Dramat").build());
        filmRepository.save(Film.builder().tytul("Komedia1").gatunek("Komedia").build());
        
        List<Film> dramaty = filmRepository.findByGatunek("Dramat");
        assertThat(dramaty).hasSize(1);
        assertThat(dramaty.get(0).getTytul()).isEqualTo("Dramat1");
    }

    @Test
    void shouldFindByTytulContainingIgnoreCase() {
        filmRepository.save(Film.builder().tytul("Władca Pierścieni").build());
        
        List<Film> result = filmRepository.findByTytulContainingIgnoreCase("władca");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTytul()).isEqualTo("Władca Pierścieni");
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmRepository.save(Film.builder().tytul("Original").build());
        film.setTytul("Updated");
        filmRepository.save(film);
        
        Film updated = filmRepository.findById(film.getId()).orElseThrow();
        assertThat(updated.getTytul()).isEqualTo("Updated");
    }

    @Test
    void shouldCountFilms() {
        long initialCount = filmRepository.count();
        filmRepository.save(Film.builder().tytul("New").build());
        assertThat(filmRepository.count()).isEqualTo(initialCount + 1);
    }

    @Test
    void shouldExistsById() {
        Film film = filmRepository.save(Film.builder().tytul("Exists").build());
        assertThat(filmRepository.existsById(film.getId())).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        assertThat(filmRepository.findById(999L)).isEmpty();
    }
}
