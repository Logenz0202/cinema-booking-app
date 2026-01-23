package pl.gdansk.cinema.cinema_booking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import pl.gdansk.cinema.cinema_booking.dto.BiletDto;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private final List<BiletDto> items = new ArrayList<>();
    private Long seansId;

    public void addToCart(Long seansId, List<BiletDto> tickets) {
        if (this.seansId != null && !this.seansId.equals(seansId)) {
            items.clear(); // Czyścimy koszyk, jeśli użytkownik zmienia seans (uproszczenie)
        }
        this.seansId = seansId;
        this.items.clear();
        this.items.addAll(tickets);
    }

    public List<BiletDto> getItems() {
        return new ArrayList<>(items);
    }

    public void clearCart() {
        items.clear();
        seansId = null;
    }

    public Double getTotalPrice() {
        return items.stream()
                .mapToDouble(BiletDto::getCena)
                .sum();
    }

    public Long getSeansId() {
        return seansId;
    }
}
