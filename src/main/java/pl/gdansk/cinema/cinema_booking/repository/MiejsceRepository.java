package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Miejsce;
import java.util.List;

public interface MiejsceRepository extends JpaRepository<Miejsce, Long> {
    List<Miejsce> findBySeansIdAndStatus(Long seansId, Miejsce.StatusMiejsca status);
}
