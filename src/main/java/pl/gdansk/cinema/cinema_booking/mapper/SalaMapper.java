package pl.gdansk.cinema.cinema_booking.mapper;

import org.mapstruct.Mapper;
import pl.gdansk.cinema.cinema_booking.dto.SalaDto;
import pl.gdansk.cinema.cinema_booking.entity.Sala;

@Mapper(componentModel = "spring")
public interface SalaMapper {
    SalaDto toDto(Sala sala);
    Sala toEntity(SalaDto salaDto);
}
