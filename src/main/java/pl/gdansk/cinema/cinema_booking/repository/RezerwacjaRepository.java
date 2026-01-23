package pl.gdansk.cinema.cinema_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gdansk.cinema.cinema_booking.entity.Rezerwacja;
import java.util.List;

public interface RezerwacjaRepository extends JpaRepository<Rezerwacja, Long> {
    List<Rezerwacja> findByUzytkownikUsername(String username);
    List<Rezerwacja> findBySeansId(Long seansId);
}
