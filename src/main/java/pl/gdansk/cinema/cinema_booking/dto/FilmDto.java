package pl.gdansk.cinema.cinema_booking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmDto {
    private Long id;

    @NotBlank(message = "Tytuł jest wymagany")
    private String tytul;

    @NotBlank(message = "Gatunek jest wymagany")
    private String gatunek;

    @Min(value = 0, message = "Wiek nie może być ujemny")
    private Integer wiek;

    private String rezyser;
    private String obsada;
    private String obrazUrl;

    @Positive(message = "Czas trwania musi być dodatni")
    private Integer czasTrwania;

    private List<SeansDto> seanse;
}
