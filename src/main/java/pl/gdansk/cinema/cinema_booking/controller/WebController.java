package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String filmList(Model model) {
        model.addAttribute("filmy", filmService.getAllFilmy());
        return "lista-filmow";
    }

    @GetMapping("/film/{id}")
    public String filmDetails(@PathVariable Long id, Model model) {
        // Pobierz DTO filmu i ustaw seanse - przydane na stronie szczegółów
        FilmDto film = filmService.getFilmById(id);
        film.setSeanse(seansService.getSeanseByFilmId(id));
        // Normalizuj trailerYoutubeId: jeśli ktoś podał pełny URL, wydobądź samo ID
        if (film.getTrailerYoutubeId() != null) {
            film.setTrailerYoutubeId(extractYoutubeId(film.getTrailerYoutubeId()));
        }
        model.addAttribute("film", film);
        // Zwracamy pełny widok szczegółów (filmy.html) z galerią i modalem
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
