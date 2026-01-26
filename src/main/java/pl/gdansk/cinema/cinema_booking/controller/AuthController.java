package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.gdansk.cinema.cinema_booking.dto.RejestracjaDto;
import pl.gdansk.cinema.cinema_booking.service.UzytkownikService;

@Controller
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AuthController {

    private final UzytkownikService uzytkownikService;

    @GetMapping("/rejestracja")
    public String pokazFormularzRejestracji(Model model) {
        log.debug("Wyświetlanie formularza rejestracji");
        model.addAttribute("uzytkownik", new RejestracjaDto());
        return "rejestracja";
    }

    @GetMapping("/login")
    public String login() {
        log.debug("Wyświetlanie strony logowania");
        return "login";
    }

    @PostMapping("/rejestracja")
    public String zarejestrujUzytkownika(@ModelAttribute("uzytkownik") RejestracjaDto dto, Model model) {
        log.info("Otrzymano żądanie rejestracji dla użytkownika: {}", dto.getUsername());
        try {
            uzytkownikService.zarejestruj(dto);
            log.info("Rejestracja pomyślna dla: {}", dto.getUsername());
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            log.warn("Błąd podczas rejestracji użytkownika {}: {}", dto.getUsername(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "rejestracja";
        }
    }
}
