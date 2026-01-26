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
    void shouldCreateSala() {
        Sala sala = Sala.builder().id(1L).numer(1).build();
        SalaDto dto = SalaDto.builder().numer(1).build();
        when(salaMapper.toEntity(dto)).thenReturn(sala);
        when(salaRepository.save(sala)).thenReturn(sala);
        when(salaMapper.toDto(sala)).thenReturn(SalaDto.builder().id(1L).numer(1).build());

        SalaDto result = salaService.createSala(dto);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateSala() {
        Sala sala = Sala.builder().id(1L).numer(1).build();
        SalaDto dto = SalaDto.builder().numer(2).build();
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(salaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(salaMapper.toDto(any())).thenReturn(dto);

        SalaDto result = salaService.updateSala(1L, dto);

        assertThat(result.getNumer()).isEqualTo(2);
    }

    @Test
    void shouldDeleteSala() {
        when(salaRepository.existsById(1L)).thenReturn(true);

        salaService.deleteSala(1L);

        verify(salaRepository).deleteById(1L);
    }
}
