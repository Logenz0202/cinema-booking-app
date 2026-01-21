package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final FilmService filmService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("filmy", filmService.getAllFilmy());
        return "index";
    }

    @GetMapping("/film/{id}")
    public String filmDetails(@PathVariable Long id, Model model) {
        model.addAttribute("film", filmService.getFilmById(id));
        return "filmy";
    }

    @GetMapping("/rezerwacja/{seansId}")
    public String reservation(@PathVariable Long seansId, Model model) {
        // Tu docelowo pobieranie seansu i miejsc
        model.addAttribute("seansId", seansId);
        return "sala";
    }
}
