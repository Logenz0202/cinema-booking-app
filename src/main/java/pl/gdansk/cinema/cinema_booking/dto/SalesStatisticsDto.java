package pl.gdansk.cinema.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatisticsDto {
    private LocalDate date;
    private Long ticketCount;
    private Double revenue;
}
