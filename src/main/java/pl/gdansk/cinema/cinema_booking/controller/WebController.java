package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.RezerwacjaService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final FilmService filmService;
    private final SeansService seansService;
    private final RezerwacjaService rezerwacjaService;
    private final pl.gdansk.cinema.cinema_booking.service.CartService cartService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String date, Model model) {
        LocalDate selectedDate;
        if (date == null) {
            selectedDate = LocalDate.now();
        } else {
            selectedDate = LocalDate.parse(date);
        }

        LocalDateTime startOfDay = selectedDate.atStartOfDay();
        List<SeansDto> seanseToday = seansService.getSeanseByDate(startOfDay);

        // Grupowanie seansów według filmu
        Map<Long, List<SeansDto>> seanseByFilm = seanseToday.stream()
                .collect(Collectors.groupingBy(SeansDto::getFilmId));

        List<FilmDto> filmyzSeansami = new ArrayList<>();
        for (Long filmId : seanseByFilm.keySet()) {
            FilmDto film = filmService.getFilmById(filmId);
            film.setSeanse(seanseByFilm.get(filmId));
            filmyzSeansami.add(film);
        }

        // Przygotowanie listy dni do wyboru (dzisiaj + 6 kolejnych dni)
        List<Map<String, String>> days = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM");
        DateTimeFormatter nameFormatter = DateTimeFormatter.ofPattern("EEE");

        for (int i = 0; i < 7; i++) {
            LocalDate d = LocalDate.now().plusDays(i);
            days.add(Map.of(
                    "date", d.toString(),
                    "displayDate", d.format(dayFormatter),
                    "dayName", i == 0 ? "Dziś" : d.format(nameFormatter)
            ));
        }

        model.addAttribute("filmy", filmyzSeansami);
        model.addAttribute("selectedDate", selectedDate.toString());
        model.addAttribute("days", days);

        return "index";
    }

    @GetMapping("/filmy-lista")
    public String filmList(@PageableDefault(size = 12, sort = "tytul") Pageable pageable, Model model) {
        Page<FilmDto> filmyPage = filmService.getAllFilmy(pageable);
        model.addAttribute("filmyPage", filmyPage);
        model.addAttribute("filmy", filmyPage.getContent());
        return "lista-filmow";
    }

    @GetMapping("/film/{id}")
    public String filmDetails(@PathVariable Long id, Model model) {
        FilmDto film = filmService.getFilmById(id);
        List<SeansDto> allUpcoming = seansService.getSeanseByFilmId(id);

        // Grupowanie seansów według daty
        Map<LocalDate, List<SeansDto>> seanseByDate = allUpcoming.stream()
                .collect(Collectors.groupingBy(s -> s.getDataGodzina().toLocalDate()));

        // Przygotowanie listy 7 dni z przypisanymi seansami
        List<Map<String, Object>> days = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM");
        DateTimeFormatter nameFormatter = DateTimeFormatter.ofPattern("EEE");

        for (int i = 0; i < 7; i++) {
            LocalDate d = LocalDate.now().plusDays(i);
            days.add(Map.of(
                    "dayName", i == 0 ? "Dziś" : d.format(nameFormatter),
                    "displayDate", d.format(dayFormatter),
                    "seanse", seanseByDate.getOrDefault(d, new ArrayList<>())
            ));
        }

        if (film.getTrailerYoutubeId() != null) {
            film.setTrailerYoutubeId(extractYoutubeId(film.getTrailerYoutubeId()));
        }

        model.addAttribute("film", film);
        model.addAttribute("repertuarDays", days);
        model.addAttribute("now", LocalDateTime.now());
        return "filmy";
    }

    @GetMapping("/rezerwacja/{seansId}")
    public String reservation(@PathVariable Long seansId, Model model) {
        SeansDto seans = seansService.getSeansById(seansId);
        List<String> occupiedSeats = rezerwacjaService.getOccupiedSeats(seansId);
        
        model.addAttribute("seans", seans);
        model.addAttribute("occupiedSeats", occupiedSeats);
        return "sala";
    }

    @PostMapping("/rezerwacja/do-koszyka")
    @org.springframework.web.bind.annotation.ResponseBody
    public String addToCart(@RequestParam Long seansId, 
                             @RequestBody List<pl.gdansk.cinema.cinema_booking.dto.BiletDto> bilety) {
        cartService.addToCart(seansId, bilety);
        return "/podsumowanie";
    }

    @GetMapping("/podsumowanie")
    public String summary(Model model) {
        if (cartService.getSeansId() == null) {
            return "redirect:/";
        }
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotalPrice());
        model.addAttribute("seans", seansService.getSeansById(cartService.getSeansId()));
        return "podsumowanie";
    }

    @PostMapping("/rezerwacja/finalizuj")
    public String finalizeReservation(java.security.Principal principal, Model model) {
        if (cartService.getSeansId() == null || principal == null) {
            return "redirect:/";
        }

        try {
            String ticketId = rezerwacjaService.finalizujRezerwacje(
                    cartService.getSeansId(),
                    cartService.getItems(),
                    principal.getName()
            );

            model.addAttribute("ticketId", ticketId);
            cartService.clearCart();
            return "potwierdzenie";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("items", cartService.getItems());
            model.addAttribute("total", cartService.getTotalPrice());
            model.addAttribute("seans", seansService.getSeansById(cartService.getSeansId()));
            return "podsumowanie";
        }
    }

    // Wydobywa ID z różnych form linków YouTube lub zwraca oryginalny przekazany identyfikator
    private String extractYoutubeId(String input) {
        if (input == null) return null;
        String s = input.trim();
        // Jeśli wygląda jak już samo ID (bez slasha i bez 'http'), zwróć.
        if (!s.contains("/") && !s.contains("?") && !s.contains("=") && !s.contains(" ")) {
            return s;
        }
        // Spróbuj dopasować typowe wzorce: youtu.be/ID lub youtube.com/watch?v=ID lub /embed/ID
        Pattern p = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|v/|.*v=))([A-Za-z0-9_-]{6,})");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        // Spróbuj pobrać parametr v z query
        try {
            URI uri = new URI(s);
            String query = uri.getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("v=")) return param.substring(2);
                }
            }
        } catch (Exception ignored) {}
        // Fallback: zwróć oryginalne wejście
        return s;
    }
}
