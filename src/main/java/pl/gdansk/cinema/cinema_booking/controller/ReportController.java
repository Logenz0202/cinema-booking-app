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
public class ReportController {
    private final StatisticsService statisticsService;

    @GetMapping("/oblozenie/csv")
    @Operation(summary = "Pobierz raport obłożenia seansów w formacie CSV")
    public ResponseEntity<byte[]> downloadOccupancyReportCsv() {
        List<Map<String, Object>> data = statisticsService.getSeansOccupancyReport();
        
        StringBuilder csv = new StringBuilder("ID;Film;Data;Zajete;Wszystkie\n");
        for (Map<String, Object> row : data) {
            csv.append(row.get("ID")).append(";")
               .append(row.get("TYTUL")).append(";")
               .append(row.get("DATA_GODZINA")).append(";")
               .append(row.get("ZAJETE_MIEJSCA")).append(";")
               .append(row.get("WSZYSTKIE_MIEJSCA")).append("\n");
        }

        byte[] content = csv.toString().getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=raport_oblozenia.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}
