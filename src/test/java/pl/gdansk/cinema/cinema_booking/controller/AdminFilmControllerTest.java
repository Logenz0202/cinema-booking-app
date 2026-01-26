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
import pl.gdansk.cinema.cinema_booking.service.FilmService;

import java.util.Collections;
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
@Import(SecurityConfig.class)
class AdminFilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.CustomUserDetailsService customUserDetailsService;

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
    @WithMockUser(roles = "ADMIN")
    void shouldListFilms() throws Exception {
        Page<FilmDto> filmPage = new PageImpl<>(Collections.emptyList());
        when(filmService.getAllFilmy(any(Pageable.class))).thenReturn(filmPage);

        mockMvc.perform(get("/admin/filmy"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/filmy-list"))
                .andExpect(model().attributeExists("filmy"))
                .andExpect(model().attributeExists("filmyPage"));
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
        when(filmService.createFilm(any())).thenReturn(FilmDto.builder().id(1L).build());

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
