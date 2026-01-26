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
public class AdminSeansController {

    private final SeansService seansService;
    private final FilmService filmService;
    private final SalaService salaService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "dataGodzina", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
        Page<SeansDto> seansePage = seansService.getSeansePaged(pageable);
        model.addAttribute("seansePage", seansePage);
        model.addAttribute("seanse", seansePage.getContent());
        return "admin/seanse-list";
    }

    @GetMapping("/nowy")
    public String createForm(Model model) {
        model.addAttribute("seansForm", new SeansForm());
        model.addAttribute("filmy", filmService.getAllFilmy());
        model.addAttribute("sale", salaService.getAllSale());
        return "admin/seans-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("seansForm") SeansForm seansForm,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        try {
            seansService.createSeans(seansForm.toDto());
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("dataGodzina", "overlap", e.getMessage());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        return "redirect:/admin/seanse";
    }

    @GetMapping("/{id}/edytuj")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("seansForm", SeansForm.fromDto(seansService.getSeansById(id)));
        model.addAttribute("filmy", filmService.getAllFilmy());
        model.addAttribute("sale", salaService.getAllSale());
        return "admin/seans-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("seansForm") SeansForm seansForm,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        try {
            seansService.updateSeans(id, seansForm.toDto());
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("dataGodzina", "overlap", e.getMessage());
            model.addAttribute("filmy", filmService.getAllFilmy());
            model.addAttribute("sale", salaService.getAllSale());
            return "admin/seans-form";
        }
        return "redirect:/admin/seanse";
    }

    @PostMapping("/{id}/usun")
    public String delete(@PathVariable Long id) {
        seansService.deleteSeans(id);
        return "redirect:/admin/seanse";
    }
}
