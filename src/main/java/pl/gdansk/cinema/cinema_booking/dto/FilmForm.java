package pl.gdansk.cinema.cinema_booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FilmForm {
    private Long id;

    @NotBlank(message = "Tytul jest wymagany")
    private String tytul;

    @NotBlank(message = "Gatunek jest wymagany")
    private String gatunek;

    @Min(value = 0, message = "Wiek nie moze byc ujemny")
    private Integer wiek;

    private String rezyser;
    private String obsada;
    private String opis;
    private String obrazUrl;

    @Positive(message = "Czas trwania musi byc dodatni")
    private Integer czasTrwania;

    private String trailerYoutubeId;
    private String galeriaUrlsRaw;

    public FilmDto toDto() {
        return FilmDto.builder()
                .id(id)
                .tytul(tytul)
                .gatunek(gatunek)
                .wiek(wiek)
                .rezyser(rezyser)
                .obsada(obsada)
                .opis(opis)
                .obrazUrl(obrazUrl)
                .czasTrwania(czasTrwania)
                .trailerYoutubeId(trailerYoutubeId)
                .galeriaUrls(parseGaleriaUrls(galeriaUrlsRaw))
                .build();
    }

    public static FilmForm fromDto(FilmDto filmDto) {
        FilmForm form = new FilmForm();
        form.setId(filmDto.getId());
        form.setTytul(filmDto.getTytul());
        form.setGatunek(filmDto.getGatunek());
        form.setWiek(filmDto.getWiek());
        form.setRezyser(filmDto.getRezyser());
        form.setObsada(filmDto.getObsada());
        form.setOpis(filmDto.getOpis());
        form.setObrazUrl(filmDto.getObrazUrl());
        form.setCzasTrwania(filmDto.getCzasTrwania());
        form.setTrailerYoutubeId(filmDto.getTrailerYoutubeId());
        if (filmDto.getGaleriaUrls() != null && !filmDto.getGaleriaUrls().isEmpty()) {
            form.setGaleriaUrlsRaw(String.join("\n", filmDto.getGaleriaUrls()));
        }
        return form;
    }

    private List<String> parseGaleriaUrls(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split("[,\\n]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}
