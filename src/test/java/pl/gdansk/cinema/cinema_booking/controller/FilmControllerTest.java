package pl.gdansk.cinema.cinema_booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@Import(SecurityConfig.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.FileService fileService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateFilm() throws Exception {
        FilmDto filmDto = FilmDto.builder().tytul("New Film").gatunek("Action").build();
        when(filmService.createFilm(any(FilmDto.class))).thenReturn(filmDto);

        mockMvc.perform(post("/api/v1/filmy")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tytul").value("New Film"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyCreateFilmForUser() throws Exception {
        FilmDto filmDto = FilmDto.builder().tytul("New Film").gatunek("Action").build();

        mockMvc.perform(post("/api/v1/filmy")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldGetFilmById() throws Exception {
        FilmDto filmDto = FilmDto.builder().id(1L).tytul("Found Film").obrazUrl("/img.png").build();
        when(filmService.getFilmById(1L)).thenReturn(filmDto);

        mockMvc.perform(get("/api/v1/filmy/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tytul").value("Found Film"))
                .andExpect(jsonPath("$.obrazUrl").value("/img.png"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateFilm() throws Exception {
        FilmDto filmDto = FilmDto.builder().tytul("Updated Film").gatunek("Drama").build();
        when(filmService.updateFilm(eq(1L), any(FilmDto.class))).thenReturn(filmDto);

        mockMvc.perform(put("/api/v1/filmy/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tytul").value("Updated Film"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteFilm() throws Exception {
        mockMvc.perform(delete("/api/v1/filmy/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
