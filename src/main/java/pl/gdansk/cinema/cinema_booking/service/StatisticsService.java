package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import pl.gdansk.cinema.cinema_booking.dto.SalesStatisticsDto;
import pl.gdansk.cinema.cinema_booking.dto.SeansOccupancyReportDto;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final JdbcTemplate jdbcTemplate;

    public List<SeansOccupancyReportDto> getSeansOccupancyReport() {
        String sql = "SELECT s.id, f.tytul, s.data_godzina, " +
                     "COUNT(CASE WHEN m.status = 'ZAJETE' THEN 1 END) as zajete_miejsca, " +
                     "(sl.rzedy * sl.miejscawrzedzie) as wszystkie_miejsca " +
                     "FROM seans s " +
                     "JOIN film f ON s.film_id = f.id " +
                     "JOIN sala sl ON s.sala_id = sl.id " +
                     "LEFT JOIN miejsce m ON s.id = m.seans_id " +
                     "GROUP BY s.id, f.tytul, s.data_godzina, sl.rzedy, sl.miejscawrzedzie";
        
        return jdbcTemplate.query(sql, new SeansOccupancyReportRowMapper());
    }

    public List<SalesStatisticsDto> getSalesStatistics() {
        String sql = "SELECT CAST(r.data_rezerwacji AS DATE) as dzien, " +
                     "COUNT(b.id) as liczba_biletow, " +
                     "SUM(b.cena) as przychod " +
                     "FROM bilet b " +
                     "JOIN rezerwacja r ON b.rezerwacja_id = r.id " +
                     "GROUP BY CAST(r.data_rezerwacji AS DATE) " +
                     "ORDER BY dzien DESC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> SalesStatisticsDto.builder()
                .date(rs.getDate("dzien").toLocalDate())
                .ticketCount(rs.getLong("liczba_biletow"))
                .revenue(rs.getDouble("przychod"))
                .build());
    }

    private static class SeansOccupancyReportRowMapper implements RowMapper<SeansOccupancyReportDto> {
        @Override
        public SeansOccupancyReportDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SeansOccupancyReportDto.builder()
                    .seansId(rs.getLong("id"))
                    .tytul(rs.getString("tytul"))
                    .dataGodzina(rs.getTimestamp("data_godzina").toLocalDateTime())
                    .zajeteMiejsca(rs.getLong("zajete_miejsca"))
                    .wszystkieMiejsca(rs.getLong("wszystkie_miejsca"))
                    .build();
        }
    }
}
