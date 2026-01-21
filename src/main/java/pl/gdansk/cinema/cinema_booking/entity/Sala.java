package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numer;
    private Integer rzedy;
    private Integer miejscaWRzedzie;
    private String opis;
}
