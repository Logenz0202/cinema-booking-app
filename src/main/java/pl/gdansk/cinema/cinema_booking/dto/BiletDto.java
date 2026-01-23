package pl.gdansk.cinema.cinema_booking.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiletDto {
    private Long id;
    private Long seansId;
    private Integer rzad;
    private Integer miejsce;
    private String typBiletu;
    private Double cena;
}
