package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;

    private LocalDateTime dataGodzina;
    private Double cenaNormalny;
    private Double cenaUlgowy;
    private Double cenaRodzinny;
}
