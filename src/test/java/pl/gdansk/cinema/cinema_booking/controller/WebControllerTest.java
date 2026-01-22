package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private pl.gdansk.cinema.cinema_booking.service.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void shouldReturnIndexPage() throws Exception {
        when(filmService.getAllFilmy()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("filmy"));
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
                .andExpect(view().name("filmy_simple"))
                .andExpect(model().attributeExists("film"))
                .andExpect(model().attribute("film", film));
    }
}
