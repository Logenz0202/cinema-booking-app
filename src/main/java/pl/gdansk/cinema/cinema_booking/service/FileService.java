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
public class FileService {

    @Value("${app.upload.dir:uploads/posters}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        Path copyLocation = Paths.get(uploadDir);
        if (!Files.exists(copyLocation)) {
            Files.createDirectories(copyLocation);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetPath = copyLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/posters/" + fileName;
    }

    public byte[] generateCsvReport(String content) {
        return content.getBytes();
    }
}
