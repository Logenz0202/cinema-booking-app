package pl.gdansk.cinema.cinema_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gdansk.cinema.cinema_booking.dto.SalaDto;
import pl.gdansk.cinema.cinema_booking.service.SalaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sale")
@RequiredArgsConstructor
@Tag(name = "Sala", description = "Zarządzanie salami kinowymi")
public class SalaController {
    private final SalaService salaService;

    @GetMapping
    @Operation(summary = "Pobierz wszystkie sale")
    public ResponseEntity<List<SalaDto>> getAllSale() {
        return ResponseEntity.ok(salaService.getAllSale());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz salę po ID")
    public ResponseEntity<SalaDto> getSalaById(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.getSalaById(id));
    }

    @PostMapping
    @Operation(summary = "Dodaj nową salę")
    public ResponseEntity<SalaDto> createSala(@Valid @RequestBody SalaDto salaDto) {
        return new ResponseEntity<>(salaService.createSala(salaDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuj istniejącą salę")
    public ResponseEntity<SalaDto> updateSala(@PathVariable Long id, @Valid @RequestBody SalaDto salaDto) {
        return ResponseEntity.ok(salaService.updateSala(id, salaDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń salę")
    public ResponseEntity<Void> deleteSala(@PathVariable Long id) {
        salaService.deleteSala(id);
        return ResponseEntity.noContent().build();
    }
}
