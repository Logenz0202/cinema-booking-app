package pl.gdansk.cinema.cinema_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Uzytkownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String haslo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "uzytkownik_role", joinColumns = @JoinColumn(name = "uzytkownik_id"))
    @Column(name = "rola")
    private Set<String> role;
}
