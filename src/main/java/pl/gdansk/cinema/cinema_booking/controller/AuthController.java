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
public class AuthController {

    private final UzytkownikService uzytkownikService;

    @GetMapping("/rejestracja")
    public String pokazFormularzRejestracji(Model model) {
        model.addAttribute("uzytkownik", new RejestracjaDto());
        return "rejestracja";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/rejestracja")
    public String zarejestrujUzytkownika(@ModelAttribute("uzytkownik") RejestracjaDto dto, Model model) {
        try {
            uzytkownikService.zarejestruj(dto);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "rejestracja";
        }
    }
}
