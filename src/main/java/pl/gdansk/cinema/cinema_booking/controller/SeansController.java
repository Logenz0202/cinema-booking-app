package pl.gdansk.cinema.cinema_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seanse")
@RequiredArgsConstructor
@Tag(name = "Seans", description = "Zarządzanie seansami")
public class SeansController {
    private final SeansService seansService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie seanse")
    public ResponseEntity<List<SeansDto>> getAllSeanse() {
        return ResponseEntity.ok(seansService.getAllSeanse());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz seans po ID")
    public ResponseEntity<SeansDto> getSeansById(@PathVariable Long id) {
        return ResponseEntity.ok(seansService.getSeansById(id));
    }

    @PostMapping
    @Operation(summary = "Dodaj nowy seans")
    public ResponseEntity<SeansDto> createSeans(@Valid @RequestBody SeansDto seansDto) {
        return new ResponseEntity<>(seansService.createSeans(seansDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuj istniejący seans")
    public ResponseEntity<SeansDto> updateSeans(@PathVariable Long id, @Valid @RequestBody SeansDto seansDto) {
        return ResponseEntity.ok(seansService.updateSeans(id, seansDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń seans")
    public ResponseEntity<Void> deleteSeans(@PathVariable Long id) {
        seansService.deleteSeans(id);
        return ResponseEntity.noContent().build();
    }
}
