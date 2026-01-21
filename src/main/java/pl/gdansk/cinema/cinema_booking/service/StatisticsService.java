package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getSeansOccupancyReport() {
        String sql = "SELECT s.id, f.tytul, s.data_godzina, " +
                     "COUNT(CASE WHEN m.status = 'ZAJETE' THEN 1 END) as zajete_miejsca, " +
                     "COUNT(m.id) as wszystkie_miejsca " +
                     "FROM seans s " +
                     "JOIN film f ON s.film_id = f.id " +
                     "LEFT JOIN miejsce m ON s.id = m.seans_id " +
                     "GROUP BY s.id, f.tytul, s.data_godzina";
        
        return jdbcTemplate.queryForList(sql);
    }
}
