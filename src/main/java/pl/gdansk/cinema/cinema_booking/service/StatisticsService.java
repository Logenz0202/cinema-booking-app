package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import pl.gdansk.cinema.cinema_booking.dto.SeansOccupancyReportDto;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final JdbcTemplate jdbcTemplate;

    public List<SeansOccupancyReportDto> getSeansOccupancyReport() {
        String sql = "SELECT s.id, f.tytul, s.data_godzina, " +
                     "COUNT(CASE WHEN m.status = 'ZAJETE' THEN 1 END) as zajete_miejsca, " +
                     "COUNT(m.id) as wszystkie_miejsca " +
                     "FROM seans s " +
                     "JOIN film f ON s.film_id = f.id " +
                     "LEFT JOIN miejsce m ON s.id = m.seans_id " +
                     "GROUP BY s.id, f.tytul, s.data_godzina";
        
        return jdbcTemplate.query(sql, new SeansOccupancyReportRowMapper());
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
