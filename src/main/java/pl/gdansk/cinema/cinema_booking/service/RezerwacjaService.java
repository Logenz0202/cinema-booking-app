package pl.gdansk.cinema.cinema_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.entity.Bilet;
import pl.gdansk.cinema.cinema_booking.repository.BiletRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RezerwacjaService {
    private final BiletRepository biletRepository;

    @Transactional(readOnly = true)
    public List<String> getOccupiedSeats(Long seansId) {
        return biletRepository.findBySeansId(seansId).stream()
                .map(bilet -> bilet.getRzad() + "-" + bilet.getMiejsce())
                .collect(Collectors.toList());
    }
}
