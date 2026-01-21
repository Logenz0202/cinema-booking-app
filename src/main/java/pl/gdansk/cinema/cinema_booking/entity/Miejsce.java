package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Miejsce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seans_id")
    private Seans seans;

    private Integer rzad;
    private Integer numer;

    @Enumerated(EnumType.STRING)
    private StatusMiejsca status;

    public enum StatusMiejsca {
        WOLNE, ZAJETE
    }
}
