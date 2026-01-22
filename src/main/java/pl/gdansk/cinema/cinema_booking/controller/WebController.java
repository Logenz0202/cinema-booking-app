package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final FilmService filmService;
    private final SeansService seansService;

    @GetMapping("/")
    public String index(Model model) {
        List<FilmDto> filmy = filmService.getAllFilmy();
        filmy.forEach(f -> f.setSeanse(seansService.getSeanseByFilmId(f.getId())));
        model.addAttribute("filmy", filmy);
        return "index";
    }

    @GetMapping("/film/{id}")
    public String filmDetails(@PathVariable Long id, Model model) {
        // Pobierz DTO filmu i ustaw seanse - przydane na stronie szczegółów
        FilmDto film = filmService.getFilmById(id);
        film.setSeanse(seansService.getSeanseByFilmId(id));
        model.addAttribute("film", film);
        // Zwracamy pełny widok szczegółów (filmy.html) z galerią i modalem
        return "filmy";
    }

    @GetMapping("/rezerwacja/{seansId}")
    public String reservation(@PathVariable Long seansId, Model model) {
        // Tu docelowo pobieranie seansu i miejsc
        model.addAttribute("seansId", seansId);
        return "sala";
    }
}
