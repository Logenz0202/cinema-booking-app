package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.gdansk.cinema.cinema_booking.dto.SeansOccupancyReportDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;
import pl.gdansk.cinema.cinema_booking.entity.Miejsce;
import pl.gdansk.cinema.cinema_booking.entity.Seans;
import pl.gdansk.cinema.cinema_booking.repository.FilmRepository;
import pl.gdansk.cinema.cinema_booking.repository.MiejsceRepository;
import pl.gdansk.cinema.cinema_booking.repository.SeansRepository;

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
}
