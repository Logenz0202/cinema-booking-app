package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    List<Film> findByGatunek(String gatunek);
    List<Film> findByTytulContainingIgnoreCase(String tytul);
}
