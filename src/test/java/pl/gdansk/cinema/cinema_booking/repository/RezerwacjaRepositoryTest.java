package pl.gdansk.cinema.cinema_booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.gdansk.cinema.cinema_booking.entity.Rezerwacja;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.entity.Uzytkownik;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RezerwacjaRepositoryTest {

    @Autowired
    private RezerwacjaRepository rezerwacjaRepository;

    @Autowired
    private UzytkownikRepository uzytkownikRepository;

    @Autowired
    private SeansRepository seansRepository;

    @Test
    void shouldFindByUzytkownikUsername() {
        Uzytkownik user = uzytkownikRepository.save(Uzytkownik.builder().username("testuser").haslo("pass").role(java.util.Set.of("USER")).build());
        rezerwacjaRepository.save(Rezerwacja.builder().uzytkownik(user).numerRezerwacji("123").build());

        List<Rezerwacja> rezerwacje = rezerwacjaRepository.findByUzytkownikUsername("testuser");

        assertThat(rezerwacje).hasSize(1);
        assertThat(rezerwacje.get(0).getUzytkownik().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFindBySeansId() {
        Seans seans = seansRepository.save(Seans.builder().build());
        rezerwacjaRepository.save(Rezerwacja.builder().seans(seans).numerRezerwacji("456").build());

        List<Rezerwacja> rezerwacje = rezerwacjaRepository.findBySeansId(seans.getId());

        assertThat(rezerwacje).hasSize(1);
    }
}
