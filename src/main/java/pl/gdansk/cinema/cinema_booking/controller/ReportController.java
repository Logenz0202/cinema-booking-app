package pl.gdansk.cinema.cinema_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.gdansk.cinema.cinema_booking.service.StatisticsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/raporty")
@RequiredArgsConstructor
@Tag(name = "Raporty", description = "Generowanie raportów i statystyk")
@lombok.extern.slf4j.Slf4j
public class ReportController {
    private final StatisticsService statisticsService;

    @GetMapping("/oblozenie/csv")
    @Operation(summary = "Pobierz raport obłożenia seansów w formacie CSV")
    public ResponseEntity<byte[]> downloadOccupancyReportCsv() {
        log.info("Żądanie pobrania raportu obłożenia w formacie CSV");
        try {
            var data = statisticsService.getSeansOccupancyReport();
            
            StringBuilder csv = new StringBuilder("ID;Film;Data;Zajete;Wszystkie\n");
            for (var row : data) {
                csv.append(row.getSeansId()).append(";")
                   .append(row.getTytul()).append(";")
                   .append(row.getDataGodzina()).append(";")
                   .append(row.getZajeteMiejsca()).append(";")
                   .append(row.getWszystkieMiejsca()).append("\n");
            }

            byte[] content = csv.toString().getBytes();
            log.info("Raport obłożenia wygenerowany pomyślnie. Liczba wierszy: {}", data.size());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=raport_oblozenia.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(content);
        } catch (Exception e) {
            log.error("Błąd podczas generowania raportu obłożenia: {}", e.getMessage());
            throw e;
        }
    }
}
