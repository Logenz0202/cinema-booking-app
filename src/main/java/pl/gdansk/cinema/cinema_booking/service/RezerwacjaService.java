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
@lombok.extern.slf4j.Slf4j
public class RezerwacjaService {
    private final BiletRepository biletRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.RezerwacjaRepository rezerwacjaRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.SeansRepository seansRepository;
    private final pl.gdansk.cinema.cinema_booking.repository.UzytkownikRepository uzytkownikRepository;

    @Transactional(readOnly = true)
    public List<String> getOccupiedSeats(Long seansId) {
        log.debug("Pobieranie zajętych miejsc dla seansu o ID: {}", seansId);
        return biletRepository.findBySeansId(seansId).stream()
                .map(bilet -> bilet.getRzad() + "-" + bilet.getMiejsce())
                .collect(Collectors.toList());
    }

    @Transactional
    public String finalizujRezerwacje(Long seansId, List<pl.gdansk.cinema.cinema_booking.dto.BiletDto> biletDtos, String username) {
        log.info("Rozpoczęcie finalizacji rezerwacji dla użytkownika: {}, seans ID: {}", username, seansId);
        pl.gdansk.cinema.cinema_booking.entity.Uzytkownik uzytkownik = uzytkownikRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Finalizacja nieudana: nie znaleziono użytkownika {}", username);
                    return new RuntimeException("Użytkownik nie znaleziony");
                });
        
        pl.gdansk.cinema.cinema_booking.entity.Seans seans = seansRepository.findById(seansId)
                .orElseThrow(() -> {
                    log.error("Finalizacja nieudana: nie znaleziono seansu o ID {}", seansId);
                    return new RuntimeException("Seans nie znaleziony");
                });

        String numerRezerwacji = "TICK-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.debug("Wygenerowano numer rezerwacji: {}", numerRezerwacji);

        pl.gdansk.cinema.cinema_booking.entity.Rezerwacja rezerwacja = pl.gdansk.cinema.cinema_booking.entity.Rezerwacja.builder()
                .uzytkownik(uzytkownik)
                .seans(seans)
                .numerRezerwacji(numerRezerwacji)
                .dataRezerwacji(java.time.LocalDateTime.now())
                .status(pl.gdansk.cinema.cinema_booking.entity.StatusRezerwacji.OPLACONA)
                .build();

        rezerwacja = rezerwacjaRepository.save(rezerwacja);

        List<pl.gdansk.cinema.cinema_booking.entity.Bilet> bilety = new java.util.ArrayList<>();
        for (pl.gdansk.cinema.cinema_booking.dto.BiletDto dto : biletDtos) {
            // Walidacja czy miejsce nie zostało zajęte w międzyczasie
            if (isSeatOccupied(seansId, dto.getRzad(), dto.getMiejsce())) {
                log.warn("Miejsce zajęte: Rząd {}, Miejsce {} dla seansu {}", dto.getRzad(), dto.getMiejsce(), seansId);
                throw new IllegalStateException("Miejsce Rząd " + dto.getRzad() + ", Miejsce " + dto.getMiejsce() + " jest już zajęte.");
            }

            bilety.add(pl.gdansk.cinema.cinema_booking.entity.Bilet.builder()
                    .seans(seans)
                    .rezerwacja(rezerwacja)
                    .rzad(dto.getRzad())
                    .miejsce(dto.getMiejsce())
                    .typBiletu(pl.gdansk.cinema.cinema_booking.entity.TypBiletu.valueOf(dto.getTypBiletu()))
                    .cena(dto.getCena())
                    .build());
        }

        biletRepository.saveAll(bilety);
        log.info("Rezerwacja {} zakończona pomyślnie dla {}", numerRezerwacji, username);
        return numerRezerwacji;
    }

    private boolean isSeatOccupied(Long seansId, Integer rzad, Integer miejsce) {
        return biletRepository.existsBySeansIdAndRzadAndMiejsce(seansId, rzad, miejsce);
    }
}
