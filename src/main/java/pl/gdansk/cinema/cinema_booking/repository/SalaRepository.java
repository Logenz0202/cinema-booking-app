package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
