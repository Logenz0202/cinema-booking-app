package pl.gdansk.cinema.cinema_booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SeansForm {
    private Long id;

    @NotNull(message = "Film jest wymagany")
    private Long filmId;

    @NotNull(message = "Sala jest wymagana")
    private Long salaId;

    @NotNull(message = "Data i godzina seansu są wymagane")
    @Future(message = "Data seansu musi być w przyszłości")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataGodzina;

    @NotNull(message = "Cena biletu normalnego jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double cenaNormalny;

    @NotNull(message = "Cena biletu ulgowego jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double cenaUlgowy;

    @NotNull(message = "Cena biletu rodzinnego jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private Double cenaRodzinny;

    public SeansDto toDto() {
        return SeansDto.builder()
                .id(id)
                .filmId(filmId)
                .salaId(salaId)
                .dataGodzina(dataGodzina)
                .cenaNormalny(cenaNormalny)
                .cenaUlgowy(cenaUlgowy)
                .cenaRodzinny(cenaRodzinny)
                .build();
    }

    public static SeansForm fromDto(SeansDto dto) {
        SeansForm form = new SeansForm();
        form.setId(dto.getId());
        form.setFilmId(dto.getFilmId());
        form.setSalaId(dto.getSalaId());
        form.setDataGodzina(dto.getDataGodzina());
        form.setCenaNormalny(dto.getCenaNormalny());
        form.setCenaUlgowy(dto.getCenaUlgowy());
        form.setCenaRodzinny(dto.getCenaRodzinny());
        return form;
    }
}
