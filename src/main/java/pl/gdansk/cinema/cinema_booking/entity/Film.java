package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tytul;
    private String gatunek;
    private Integer wiek;
    private String rezyser;
    private String obsada;
    private String obrazUrl;
    private Integer czasTrwania; // w minutach
}
