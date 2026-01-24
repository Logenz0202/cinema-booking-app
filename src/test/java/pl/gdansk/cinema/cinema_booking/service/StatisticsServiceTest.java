package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.gdansk.cinema.cinema_booking.dto.SalesStatisticsDto;
import pl.gdansk.cinema.cinema_booking.dto.SeansOccupancyReportDto;
import pl.gdansk.cinema.cinema_booking.entity.*;
import pl.gdansk.cinema.cinema_booking.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(StatisticsService.class)
class StatisticsServiceTest {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SeansRepository seansRepository;

    @Autowired
    private MiejsceRepository miejsceRepository;

    @Autowired
    private BiletRepository biletRepository;

    @Autowired
    private RezerwacjaRepository rezerwacjaRepository;

    @Test
    void shouldReturnOccupancyReport() {
        // Given
        Film film = filmRepository.save(Film.builder().tytul("Interstellar").build());
        Seans seans = seansRepository.save(Seans.builder().film(film).dataGodzina(LocalDateTime.now()).build());
        
        miejsceRepository.save(Miejsce.builder().seans(seans).status(Miejsce.StatusMiejsca.ZAJETE).rzad(1).numer(1).build());
        miejsceRepository.save(Miejsce.builder().seans(seans).status(Miejsce.StatusMiejsca.WOLNE).rzad(1).numer(2).build());

        // When
        List<SeansOccupancyReportDto> report = statisticsService.getSeansOccupancyReport();

        // Then
        assertThat(report).isNotEmpty();
        SeansOccupancyReportDto row = report.stream()
                .filter(r -> r.getTytul().equals("Interstellar"))
                .findFirst()
                .orElseThrow();
        
        assertThat(row.getZajeteMiejsca()).isEqualTo(1L);
        assertThat(row.getWszystkieMiejsca()).isEqualTo(2L);
    }

    @Test
    void shouldReturnSalesStatistics() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Rezerwacja rez = rezerwacjaRepository.save(Rezerwacja.builder()
                .dataRezerwacji(now)
                .numerRezerwacji("TEST-123")
                .status(StatusRezerwacji.OPLACONA)
                .build());
        
        biletRepository.save(Bilet.builder().rezerwacja(rez).cena(25.0).typBiletu(TypBiletu.NORMALNY).build());
        biletRepository.save(Bilet.builder().rezerwacja(rez).cena(15.0).typBiletu(TypBiletu.ULGOWY).build());

        // When
        List<SalesStatisticsDto> stats = statisticsService.getSalesStatistics();

        // Then
        assertThat(stats).isNotEmpty();
        SalesStatisticsDto dayStat = stats.stream()
                .filter(s -> s.getDate().equals(now.toLocalDate()))
                .findFirst()
                .orElseThrow();
        
        assertThat(dayStat.getTicketCount()).isEqualTo(2L);
        assertThat(dayStat.getRevenue()).isEqualTo(40.0);
    }
}
