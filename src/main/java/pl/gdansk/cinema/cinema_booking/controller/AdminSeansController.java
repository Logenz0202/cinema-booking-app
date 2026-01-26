package pl.gdansk.cinema.cinema_booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.dto.SeansForm;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SalaService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

@Controller
@RequestMapping("/admin/seanse")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AdminSeansController {

    private final SeansService seansService;
    private final FilmService filmService;
    private final SalaService salaService;

    @GetMapping
    public String list(@PageableDefault(size = 12, sort = "dataGodzina", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        log.debug("Wyświetlanie listy seansów w panelu admina: {}", pageable);
        Page<SeansDto> seansePage = seansService.getSeansePaged(pageable);
        model.addAttribute("seansePage", seansePage);
        model.addAttribute("seanse", seansePage.getContent());
        return "admin/seanse-list";
    }

    @GetMapping("/nowy")
    public String createForm(Model model) {
        log.debug("Wyświetlanie formularza nowego seansu");
        model.addAttribute("seansForm", new SeansForm());
        model.addAttribute("filmy", filmService.getAllFilmy());
        model.addAttribute("sale", salaService.getAllSale());
        return "admin/seans-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("seansForm") SeansForm seansForm,
                         BindingResult bindingResult, Model model) {
        log.info("Próba utworzenia nowego seansu dla filmu ID: {}", seansForm.getFilmId());
        if (bindingResult.hasErrors()) {
            log.warn("Błąd walidacji podczas tworzenia seansu: {}", bindingResult.getAllErrors());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        try {
            SeansDto created = seansService.createSeans(seansForm.toDto());
            log.info("Seans utworzony pomyślnie z ID: {}", created.getId());
        } catch (IllegalStateException e) {
            log.warn("Błąd biznesowy podczas tworzenia seansu: {}", e.getMessage());
            bindingResult.rejectValue("dataGodzina", "overlap", e.getMessage());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        return "redirect:/admin/seanse";
    }

    @GetMapping("/{id}/edytuj")
    public String editForm(@PathVariable Long id, Model model) {
        log.debug("Wyświetlanie formularza edycji seansu o ID: {}", id);
        model.addAttribute("seansForm", SeansForm.fromDto(seansService.getSeansById(id)));
        model.addAttribute("filmy", filmService.getAllFilmy());
        model.addAttribute("sale", salaService.getAllSale());
        return "admin/seans-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("seansForm") SeansForm seansForm,
                         BindingResult bindingResult, Model model) {
        log.info("Próba aktualizacji seansu o ID: {}", id);
        if (bindingResult.hasErrors()) {
            log.warn("Błąd walidacji podczas aktualizacji seansu {}: {}", id, bindingResult.getAllErrors());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        try {
            seansService.updateSeans(id, seansForm.toDto());
            log.info("Seans o ID: {} zaktualizowany pomyślnie", id);
        } catch (IllegalStateException e) {
            log.warn("Błąd biznesowy podczas aktualizacji seansu {}: {}", id, e.getMessage());
            bindingResult.rejectValue("dataGodzina", "overlap", e.getMessage());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        return "redirect:/admin/seanse";
    }

    @PostMapping("/{id}/usun")
    public String delete(@PathVariable Long id) {
        log.info("Próba usunięcia seansu o ID: {}", id);
        seansService.deleteSeans(id);
        log.info("Seans o ID: {} został usunięty pomyślnie", id);
        return "redirect:/admin/seanse";
    }
}
