package pl.gdansk.cinema.cinema_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejestracjaDto {
    private String username;
    private String haslo;

    // Aby dodać więcej parametrów w przyszłości (np. email):
    // 1. Dodaj pole tutaj: private String email;
    // 2. Dodaj pole w klasie encji Uzytkownik.
    // 3. Zaktualizuj formularz HTML rejestracja.html.
    // 4. Zaktualizuj metodę zarejestruj w UzytkownikService.
}
