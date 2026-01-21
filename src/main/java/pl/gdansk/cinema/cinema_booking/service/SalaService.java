package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.dto.SalaDto;
import pl.gdansk.cinema.cinema_booking.entity.Sala;
import pl.gdansk.cinema.cinema_booking.exception.ResourceNotFoundException;
import pl.gdansk.cinema.cinema_booking.mapper.SalaMapper;
import pl.gdansk.cinema.cinema_booking.repository.SalaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaService {
    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    @Transactional(readOnly = true)
    public List<SalaDto> getAllSale() {
        return salaRepository.findAll().stream()
                .map(salaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalaDto getSalaById(Long id) {
        return salaRepository.findById(id)
                .map(salaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));
    }

    @Transactional
    public SalaDto createSala(SalaDto salaDto) {
        Sala sala = salaMapper.toEntity(salaDto);
        return salaMapper.toDto(salaRepository.save(sala));
    }

    @Transactional
    public SalaDto updateSala(Long id, SalaDto salaDto) {
        Sala existingSala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));
        
        existingSala.setNumer(salaDto.getNumer());
        existingSala.setRzedy(salaDto.getRzedy());
        existingSala.setMiejscaWRzedzie(salaDto.getMiejscaWRzedzie());
        existingSala.setOpis(salaDto.getOpis());
        
        return salaMapper.toDto(salaRepository.save(existingSala));
    }

    @Transactional
    public void deleteSala(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono sali o ID: " + id);
        }
        salaRepository.deleteById(id);
    }
}
