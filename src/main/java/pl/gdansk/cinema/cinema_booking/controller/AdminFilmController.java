package pl.gdansk.cinema.cinema_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.FilmForm;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

@Controller
@RequestMapping("/admin/filmy")
@RequiredArgsConstructor
public class AdminFilmController {

    private final FilmService filmService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("filmy", filmService.getAllFilmy());
        return "admin/filmy-list";
    }

    @GetMapping("/nowy")
    public String createForm(Model model) {
        model.addAttribute("filmForm", new FilmForm());
        return "admin/film-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("filmForm") FilmForm filmForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/film-form";
        }
        filmService.createFilm(filmForm.toDto());
        return "redirect:/admin/filmy";
    }

    @GetMapping("/{id}/edytuj")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("filmForm", FilmForm.fromDto(filmService.getFilmById(id)));
        return "admin/film-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("filmForm") FilmForm filmForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/film-form";
        }
        filmService.updateFilm(id, filmForm.toDto());
        return "redirect:/admin/filmy";
    }

    @PostMapping("/{id}/usun")
    public String delete(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return "redirect:/admin/filmy";
    }
}
