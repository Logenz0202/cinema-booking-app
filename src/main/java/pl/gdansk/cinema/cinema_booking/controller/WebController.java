package pl.gdansk.cinema.cinema_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // Tu docelowo pobieranie seansu i miejsc
        model.addAttribute("seansId", seansId);
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
