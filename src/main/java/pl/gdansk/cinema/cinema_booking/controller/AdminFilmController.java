package pl.gdansk.cinema.cinema_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.dto.FilmForm;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

@Controller
@RequestMapping("/admin/filmy")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AdminFilmController {

    private final FilmService filmService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "tytul") Pageable pageable,
                       Model model) {
        log.debug("Wyświetlanie listy filmów w panelu admina: {}", pageable);
        Page<FilmDto> filmyPage = filmService.getAllFilmy(pageable);
        model.addAttribute("filmyPage", filmyPage);
        model.addAttribute("filmy", filmyPage.getContent());
        return "admin/filmy-list";
    }

    @GetMapping("/nowy")
    public String createForm(Model model) {
        log.debug("Wyświetlanie formularza nowego filmu");
        model.addAttribute("filmForm", new FilmForm());
        return "admin/film-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("filmForm") FilmForm filmForm,
                         BindingResult bindingResult) {
        log.info("Próba utworzenia nowego filmu: {}", filmForm.getTytul());
        if (bindingResult.hasErrors()) {
            log.warn("Błąd walidacji podczas tworzenia filmu: {}", bindingResult.getAllErrors());
            return "admin/film-form";
        }
        FilmDto created = filmService.createFilm(filmForm.toDto());
        log.info("Film utworzony pomyślnie z ID: {}", created.getId());
        return "redirect:/admin/filmy";
    }

    @GetMapping("/{id}/edytuj")
    public String editForm(@PathVariable Long id, Model model) {
        log.debug("Wyświetlanie formularza edycji filmu o ID: {}", id);
        model.addAttribute("filmForm", FilmForm.fromDto(filmService.getFilmById(id)));
        return "admin/film-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("filmForm") FilmForm filmForm,
                         BindingResult bindingResult) {
        log.info("Próba aktualizacji filmu o ID: {}", id);
        if (bindingResult.hasErrors()) {
            log.warn("Błąd walidacji podczas aktualizacji filmu {}: {}", id, bindingResult.getAllErrors());
            return "admin/film-form";
        }
        filmService.updateFilm(id, filmForm.toDto());
        log.info("Film o ID: {} zaktualizowany pomyślnie", id);
        return "redirect:/admin/filmy";
    }

    @PostMapping("/{id}/usun")
    public String delete(@PathVariable Long id) {
        log.info("Próba usunięcia filmu o ID: {}", id);
        filmService.deleteFilm(id);
        log.info("Film o ID: {} został usunięty pomyślnie", id);
        return "redirect:/admin/filmy";
    }
}
