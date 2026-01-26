package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
@Import(SecurityConfig.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    @MockitoBean
    private SeansService seansService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.RezerwacjaService rezerwacjaService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.CartService cartService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.security.ImpersonationFilter impersonationFilter;

    @org.junit.jupiter.api.BeforeEach
    void setup() throws jakarta.servlet.ServletException, java.io.IOException {
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletRequest request = invocation.getArgument(0);
            jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(impersonationFilter).doFilter(any(), any(), any());
    }

    @Test
    @WithMockUser
    void shouldReturnIndexPage() throws Exception {
        when(seansService.getSeanseByDate(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("filmy"))
                .andExpect(model().attributeExists("days"))
                .andExpect(model().attributeExists("selectedDate"));
    }

    @Test
    @WithMockUser
    void shouldReturnIndexPageWithSpecificDate() throws Exception {
        when(seansService.getSeanseByDate(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/").param("date", "2026-01-25"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("selectedDate", "2026-01-25"));
    }

    @Test
    @WithMockUser
    void shouldReturnFilmDetailsPage() throws Exception {
        FilmDto film = FilmDto.builder()
                .id(1L)
                .tytul("Test Film")
                .gatunek("Action")
                .wiek(12)
                .czasTrwania(120)
                .obrazUrl("/test.png")
                .trailerYoutubeId("testId")
                .galeriaUrls(Collections.singletonList("/img1.png"))
                .build();

        when(filmService.getFilmById(1L)).thenReturn(film);

        mockMvc.perform(get("/film/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("filmy"))
                .andExpect(model().attributeExists("film"))
                .andExpect(model().attributeExists("repertuarDays"))
                .andExpect(model().attribute("film", film));
    }

    @Test
    @WithMockUser
    void shouldReturnFilmListPage() throws Exception {
        Page<FilmDto> filmPage = new PageImpl<>(Collections.emptyList());
        when(filmService.getAllFilmy(any(Pageable.class))).thenReturn(filmPage);

        mockMvc.perform(get("/filmy-lista"))
                .andExpect(status().isOk())
                .andExpect(view().name("lista-filmow"))
                .andExpect(model().attributeExists("filmy"))
                .andExpect(model().attributeExists("filmyPage"));
    }

    @Test
    @WithMockUser
    void shouldReturnFilmDetailsPageWithDifferentYoutubeFormats() throws Exception {
        String[] youtubeInputs = {
            "https://www.youtube.com/watch?v=Xithigfg7dA",
            "https://youtu.be/Xithigfg7dA",
            "https://www.youtube.com/embed/Xithigfg7dA",
            "Xithigfg7dA"
        };

        for (String input : youtubeInputs) {
            FilmDto film = FilmDto.builder().id(1L).tytul("Test").trailerYoutubeId(input).build();
            when(filmService.getFilmById(1L)).thenReturn(film);

            mockMvc.perform(get("/film/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("film", org.hamcrest.Matchers.hasProperty("trailerYoutubeId", org.hamcrest.Matchers.is("Xithigfg7dA"))));
        }
    }

    @Test
    @WithMockUser
    void shouldAddToCart() throws Exception {
        mockMvc.perform(post("/rezerwacja/do-koszyka")
                        .param("seansId", "1")
                        .content("[]")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("/podsumowanie"));
    }

    @Test
    @WithMockUser
    void shouldRedirectSummaryToHomeWhenCartEmpty() throws Exception {
        when(cartService.getSeansId()).thenReturn(null);

        mockMvc.perform(get("/podsumowanie"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser
    void shouldReturnSummaryPage() throws Exception {
        when(cartService.getSeansId()).thenReturn(1L);
        when(seansService.getSeansById(1L)).thenReturn(SeansDto.builder().id(1L).build());

        mockMvc.perform(get("/podsumowanie"))
                .andExpect(status().isOk())
                .andExpect(view().name("podsumowanie"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attributeExists("seans"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldFinalizeReservation() throws Exception {
        when(cartService.getSeansId()).thenReturn(1L);
        when(rezerwacjaService.finalizujRezerwacje(eq(1L), any(), eq("testuser"))).thenReturn("TICK-123");

        mockMvc.perform(post("/rezerwacja/finalizuj").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("potwierdzenie"))
                .andExpect(model().attribute("ticketId", "TICK-123"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnSummaryWithErrorWhenFinalizationFails() throws Exception {
        when(cartService.getSeansId()).thenReturn(1L);
        when(rezerwacjaService.finalizujRezerwacje(any(), any(), any())).thenThrow(new RuntimeException("Błąd"));
        when(seansService.getSeansById(1L)).thenReturn(SeansDto.builder().id(1L).build());

        mockMvc.perform(post("/rezerwacja/finalizuj").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("podsumowanie"))
                .andExpect(model().attribute("error", "Błąd"));
    }

    @Test
    @WithMockUser
    void shouldRedirectToHomeWhenFinalizingWithEmptyCart() throws Exception {
        when(cartService.getSeansId()).thenReturn(null);

        mockMvc.perform(post("/rezerwacja/finalizuj").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
