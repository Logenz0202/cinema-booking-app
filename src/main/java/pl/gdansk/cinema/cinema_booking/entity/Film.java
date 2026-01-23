package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    private String opis;
    private String obrazUrl;
    private Integer czasTrwania; // w minutach

    private String trailerYoutubeId;

    @ElementCollection
    @CollectionTable(name = "film_galeria", joinColumns = @JoinColumn(name = "film_id"))
    @Column(name = "image_url")
    private List<String> galeriaUrls;
}
