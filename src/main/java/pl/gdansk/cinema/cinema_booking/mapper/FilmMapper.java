package pl.gdansk.cinema.cinema_booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    @Mapping(target = "obrazUrl", source = "obrazUrl")
    @Mapping(target = "czasTrwania", source = "czasTrwania")
    FilmDto toDto(Film film);

    @Mapping(target = "obrazUrl", source = "obrazUrl")
    @Mapping(target = "czasTrwania", source = "czasTrwania")
    Film toEntity(FilmDto filmDto);
}
