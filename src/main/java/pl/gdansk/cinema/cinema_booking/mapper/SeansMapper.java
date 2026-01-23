package pl.gdansk.cinema.cinema_booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.entity.Seans;

@Mapper(componentModel = "spring")
public interface SeansMapper {
    @Mapping(source = "film.id", target = "filmId")
    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "film.tytul", target = "filmTytul")
    @Mapping(source = "sala.numer", target = "salaNumer")
    @Mapping(source = "sala.rzedy", target = "salaRzedy")
    @Mapping(source = "sala.miejscaWRzedzie", target = "salaMiejscaWRzedzie")
    SeansDto toDto(Seans seans);

    @Mapping(source = "filmId", target = "film.id")
    @Mapping(source = "salaId", target = "sala.id")
    Seans toEntity(SeansDto seansDto);
}
