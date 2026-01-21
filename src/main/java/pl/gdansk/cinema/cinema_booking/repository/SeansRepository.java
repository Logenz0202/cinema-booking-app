package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import java.time.LocalDateTime;
import java.util.List;

public interface SeansRepository extends JpaRepository<Seans, Long> {
    List<Seans> findByFilmIdAndDataGodzinaAfter(Long filmId, LocalDateTime data);
}
