package pl.gdansk.cinema.cinema_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/filmy")
@RequiredArgsConstructor
@Tag(name = "Film", description = "Zarządzanie filmami")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie filmy z paginacją")
    public ResponseEntity<Page<FilmDto>> getAllFilmy(Pageable pageable) {
        return ResponseEntity.ok(filmService.getAllFilmy(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz film po ID")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Long id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @PostMapping
    @Operation(summary = "Dodaj nowy film")
    public ResponseEntity<FilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
        return new ResponseEntity<>(filmService.createFilm(filmDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuj istniejący film")
    public ResponseEntity<FilmDto> updateFilm(@PathVariable Long id, @Valid @RequestBody FilmDto filmDto) {
        return ResponseEntity.ok(filmService.updateFilm(id, filmDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń film")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }
}
