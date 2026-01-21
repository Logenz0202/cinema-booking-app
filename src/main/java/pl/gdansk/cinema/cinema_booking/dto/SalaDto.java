package pl.gdansk.cinema.cinema_booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaDto {
    private Long id;

    @NotNull(message = "Numer sali jest wymagany")
    private Integer numer;

    @NotNull(message = "Liczba rzędów jest wymagana")
    @Min(value = 1, message = "Liczba rzędów musi być większa od 0")
    private Integer rzedy;

    @NotNull(message = "Liczba miejsc w rzędzie jest wymagana")
    @Min(value = 1, message = "Liczba miejsc w rzędzie musi być większa od 0")
    private Integer miejscaWRzedzie;

    private String opis;
}
