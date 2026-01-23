package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Bilet;
import java.util.List;

public interface BiletRepository extends JpaRepository<Bilet, Long> {
    List<Bilet> findBySeansId(Long seansId);
}
