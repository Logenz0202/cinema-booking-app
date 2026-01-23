package pl.gdansk.cinema.cinema_booking.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RezerwacjaDto {
    private Long id;
    private String uzytkownikUsername;
    private Long seansId;
    private String numerRezerwacji;
    private LocalDateTime dataRezerwacji;
    private String status;
    private List<BiletDto> bilety;
}
