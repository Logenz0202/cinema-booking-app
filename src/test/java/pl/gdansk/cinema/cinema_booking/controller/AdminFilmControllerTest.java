package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.dto.FilmDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminFilmController.class)
class AdminFilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListFilms() throws Exception {
        when(filmService.getAllFilmy()).thenReturn(List.of());

        mockMvc.perform(get("/admin/filmy"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/filmy-list"))
                .andExpect(model().attributeExists("filmy"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/admin/filmy/nowy"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/film-form"))
                .andExpect(model().attributeExists("filmForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateFilm() throws Exception {
        mockMvc.perform(post("/admin/filmy")
                        .param("tytul", "New Film")
                        .param("gatunek", "Action")
                        .param("wiek", "12")
                        .param("czasTrwania", "120")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/filmy"));

        verify(filmService).createFilm(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowEditForm() throws Exception {
        FilmDto dto = FilmDto.builder().id(1L).tytul("Test").build();
        when(filmService.getFilmById(1L)).thenReturn(dto);

        mockMvc.perform(get("/admin/filmy/1/edytuj"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/film-form"))
                .andExpect(model().attributeExists("filmForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateFilm() throws Exception {
        mockMvc.perform(post("/admin/filmy/1")
                        .param("tytul", "Updated")
                        .param("gatunek", "Drama")
                        .param("wiek", "16")
                        .param("czasTrwania", "90")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/filmy"));

        verify(filmService).updateFilm(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteFilm() throws Exception {
        mockMvc.perform(post("/admin/filmy/1/usun")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/filmy"));

        verify(filmService).deleteFilm(1L);
    }
}
