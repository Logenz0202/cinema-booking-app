package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import java.time.LocalDateTime;
import java.util.List;

public interface SeansRepository extends JpaRepository<Seans, Long> {
    List<Seans> findByFilmIdAndDataGodzinaAfterOrderByDataGodzinaAsc(Long filmId, LocalDateTime data);
    List<Seans> findBySalaIdAndDataGodzinaBetween(Long salaId, LocalDateTime start, LocalDateTime end);
    List<Seans> findByDataGodzinaBetween(LocalDateTime start, LocalDateTime end);
    List<Seans> findByFilmIdAndDataGodzinaBetween(Long filmId, LocalDateTime start, LocalDateTime end);
}
