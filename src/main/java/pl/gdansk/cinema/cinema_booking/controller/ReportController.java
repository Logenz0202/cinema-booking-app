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

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=raport_oblozenia.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}
