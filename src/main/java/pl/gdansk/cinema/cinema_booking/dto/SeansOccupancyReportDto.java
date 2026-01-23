package pl.gdansk.cinema.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeansOccupancyReportDto {
    private Long seansId;
    private String tytul;
    private LocalDateTime dataGodzina;
    private Long zajeteMiejsca;
    private Long wszystkieMiejsca;
}
