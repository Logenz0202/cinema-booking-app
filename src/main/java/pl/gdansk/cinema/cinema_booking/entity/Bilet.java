package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bilet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seans_id")
    private Seans seans;

    @ManyToOne
    @JoinColumn(name = "rezerwacja_id")
    private Rezerwacja rezerwacja;

    private Integer rzad;
    private Integer miejsce;

    @Enumerated(EnumType.STRING)
    private TypBiletu typBiletu;

    private Double cena;
}
