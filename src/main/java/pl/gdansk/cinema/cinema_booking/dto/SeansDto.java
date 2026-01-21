package pl.gdansk.cinema.cinema_booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeansDto {
    private Long id;

    @NotNull(message = "ID filmu jest wymagane")
    private Long filmId;

    @NotNull(message = "ID sali jest wymagane")
    private Long salaId;

    @NotNull(message = "Data i godzina seansu są wymagane")
    @Future(message = "Data seansu musi być w przyszłości")
    private LocalDateTime dataGodzina;

    @NotNull(message = "Cena biletu normalnego jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double cenaNormalny;

    @NotNull(message = "Cena biletu ulgowego jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double cenaUlgowy;

    private String filmTytul;
    private Integer salaNumer;
}
