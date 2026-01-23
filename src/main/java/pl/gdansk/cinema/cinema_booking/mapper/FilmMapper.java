package pl.gdansk.cinema.cinema_booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    @Mapping(target = "obrazUrl", source = "obrazUrl")
    @Mapping(target = "czasTrwania", source = "czasTrwania")
    @Mapping(target = "trailerYoutubeId", source = "trailerYoutubeId")
    @Mapping(target = "galeriaUrls", source = "galeriaUrls")
    @Mapping(target = "opis", source = "opis")
    @Mapping(target = "seanse", ignore = true)
    FilmDto toDto(Film film);

    @Mapping(target = "obrazUrl", source = "obrazUrl")
    @Mapping(target = "czasTrwania", source = "czasTrwania")
    @Mapping(target = "trailerYoutubeId", source = "trailerYoutubeId")
    @Mapping(target = "galeriaUrls", source = "galeriaUrls")
    @Mapping(target = "opis", source = "opis")
    Film toEntity(FilmDto filmDto);
}
