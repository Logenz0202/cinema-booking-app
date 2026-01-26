package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private final FileService fileService = new FileService();

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveImage() throws IOException {
        // given
        String uploadPath = tempDir.toString();
        ReflectionTestUtils.setField(fileService, "uploadDir", uploadPath);
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.png", "image/png", "some image data".getBytes());

        // when
        String resultPath = fileService.saveImage(file);

        // then
        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("/uploads/posters/"));
        assertTrue(resultPath.endsWith("_test.png"));

        // verify file exists
        String fileName = resultPath.substring("/uploads/posters/".length());
        Path savedFile = tempDir.resolve(fileName);
        assertTrue(Files.exists(savedFile));
        assertArrayEquals("some image data".getBytes(), Files.readAllBytes(savedFile));
    }

    @Test
    void shouldGenerateCsvReport() {
        // given
        String content = "id;name\n1;test";

        // when
        byte[] result = fileService.generateCsvReport(content);

        // then
        assertArrayEquals(content.getBytes(), result);
    }

    @Test
    void shouldThrowExceptionWhenFileSaveFails() throws IOException {
        // given
        ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
        org.springframework.web.multipart.MultipartFile file = org.mockito.Mockito.mock(org.springframework.web.multipart.MultipartFile.class);
        org.mockito.Mockito.when(file.getOriginalFilename()).thenReturn("test.png");
        org.mockito.Mockito.when(file.getInputStream()).thenThrow(new IOException("Disk full"));

        // when & then
        assertThrows(IOException.class, () -> fileService.saveImage(file));
    }
}
