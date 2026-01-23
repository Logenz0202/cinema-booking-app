package pl.gdansk.cinema.cinema_booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.gdansk.cinema.cinema_booking.dto.BiletDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void shouldAddItemsToCart() {
        // Given
        Long seansId = 1L;
        BiletDto ticket = BiletDto.builder().id(1L).cena(25.0).typBiletu("NORMALNY").build();
        List<BiletDto> tickets = List.of(ticket);

        // When
        cartService.addToCart(seansId, tickets);

        // Then
        assertEquals(1, cartService.getItems().size());
        assertEquals(25.0, cartService.getTotalPrice());
        assertEquals(seansId, cartService.getSeansId());
    }

    @Test
    void shouldClearCartWhenSeansChanges() {
        // Given
        cartService.addToCart(1L, List.of(BiletDto.builder().cena(25.0).build()));

        // When
        cartService.addToCart(2L, List.of(BiletDto.builder().cena(20.0).build()));

        // Then
        assertEquals(1, cartService.getItems().size());
        assertEquals(20.0, cartService.getTotalPrice());
        assertEquals(2L, cartService.getSeansId());
    }

    @Test
    void shouldClearCartManually() {
        // Given
        cartService.addToCart(1L, List.of(BiletDto.builder().cena(25.0).build()));

        // When
        cartService.clearCart();

        // Then
        assertTrue(cartService.getItems().isEmpty());
        assertNull(cartService.getSeansId());
    }
}
