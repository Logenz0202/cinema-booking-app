package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rezerwacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uzytkownik_id")
    private Uzytkownik uzytkownik;

    @ManyToOne
    @JoinColumn(name = "seans_id")
    private Seans seans;

    private String numerRezerwacji;
    private LocalDateTime dataRezerwacji;

    @Enumerated(EnumType.STRING)
    private StatusRezerwacji status;

    @OneToMany(mappedBy = "rezerwacja", cascade = CascadeType.ALL)
    private List<Bilet> bilety;
}
