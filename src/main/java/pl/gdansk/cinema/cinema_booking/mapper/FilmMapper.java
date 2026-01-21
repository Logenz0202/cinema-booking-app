package pl.gdansk.cinema.cinema_booking.mapper;

import org.mapstruct.Mapper;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.entity.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    FilmDto toDto(Film film);
    Film toEntity(FilmDto filmDto);
}
