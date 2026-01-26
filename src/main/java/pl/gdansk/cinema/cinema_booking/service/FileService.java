package pl.gdansk.cinema.cinema_booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
public class FileService {

    @Value("${app.upload.dir:uploads/posters}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        log.info("Próba zapisu pliku: {}", file.getOriginalFilename());
        Path copyLocation = Paths.get(uploadDir);
        if (!Files.exists(copyLocation)) {
            log.debug("Tworzenie katalogu dla uploadu: {}", uploadDir);
            Files.createDirectories(copyLocation);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetPath = copyLocation.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Plik zapisany pomyślnie jako: {}", fileName);
        } catch (IOException e) {
            log.error("Błąd podczas kopiowania pliku {}: {}", file.getOriginalFilename(), e.getMessage());
            throw e;
        }

        return "/uploads/posters/" + fileName;
    }

    public byte[] generateCsvReport(String content) {
        log.debug("Generowanie raportu CSV");
        return content.getBytes();
    }
}
