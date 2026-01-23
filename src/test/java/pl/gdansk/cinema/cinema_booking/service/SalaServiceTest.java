package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.gdansk.cinema.cinema_booking.dto.SalaDto;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.mapper.SalaMapper;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private SalaMapper salaMapper;

    @InjectMocks
    private SalaService salaService;

    @Test
    void shouldGetAllSale() {
        when(salaRepository.findAll()).thenReturn(List.of(new Sala()));
        when(salaMapper.toDto(any())).thenReturn(new SalaDto());

        List<SalaDto> result = salaService.getAllSale();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetSalaById() {
        Sala sala = Sala.builder().id(1L).numer(1).build();
        SalaDto dto = SalaDto.builder().id(1L).numer(1).build();
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(salaMapper.toDto(sala)).thenReturn(dto);

        SalaDto result = salaService.getSalaById(1L);

        assertThat(result.getNumer()).isEqualTo(1);
    }
}
