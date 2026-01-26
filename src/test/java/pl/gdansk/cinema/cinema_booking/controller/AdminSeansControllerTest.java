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
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.service.FilmService;
import pl.gdansk.cinema.cinema_booking.service.SalaService;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

@WebMvcTest(AdminSeansController.class)
@Import(SecurityConfig.class)
class AdminSeansControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeansService seansService;

    @MockitoBean
    private FilmService filmService;

    @MockitoBean
    private SalaService salaService;

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
    void shouldListSeanse() throws Exception {
        Page<SeansDto> seansePage = new PageImpl<>(Collections.emptyList());
        when(seansService.getSeansePaged(any(Pageable.class))).thenReturn(seansePage);

        mockMvc.perform(get("/admin/seanse"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seanse-list"))
                .andExpect(model().attributeExists("seanse"))
                .andExpect(model().attributeExists("seansePage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateForm() throws Exception {
        when(filmService.getAllFilmy()).thenReturn(List.of());
        when(salaService.getAllSale()).thenReturn(List.of());

        mockMvc.perform(get("/admin/seanse/nowy"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seans-form"))
                .andExpect(model().attributeExists("seansForm"))
                .andExpect(model().attributeExists("filmy"))
                .andExpect(model().attributeExists("sale"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateSeans() throws Exception {
        String dataStr = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES).toString();
        mockMvc.perform(post("/admin/seanse")
                        .param("filmId", "1")
                        .param("salaId", "1")
                        .param("dataGodzina", dataStr)
                        .param("cenaNormalny", "25.0")
                        .param("cenaUlgowy", "20.0")
                        .param("cenaRodzinny", "60.0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/seanse"));

        verify(seansService).createSeans(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormOnError() throws Exception {
        mockMvc.perform(post("/admin/seanse")
                        .param("filmId", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seans-form"))
                .andExpect(model().attributeHasFieldErrors("seansForm", "filmId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormOnOverlap() throws Exception {
        String dataStr = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES).toString();
        when(seansService.createSeans(any())).thenThrow(new IllegalStateException("Overlap error"));

        mockMvc.perform(post("/admin/seanse")
                        .param("filmId", "1")
                        .param("salaId", "1")
                        .param("dataGodzina", dataStr)
                        .param("cenaNormalny", "25.0")
                        .param("cenaUlgowy", "20.0")
                        .param("cenaRodzinny", "60.0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seans-form"))
                .andExpect(model().attributeHasFieldErrors("seansForm", "dataGodzina"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowEditForm() throws Exception {
        SeansDto dto = SeansDto.builder().id(1L).filmId(1L).salaId(1L).dataGodzina(LocalDateTime.now().plusDays(1)).build();
        when(seansService.getSeansById(1L)).thenReturn(dto);
        when(filmService.getAllFilmy()).thenReturn(List.of());
        when(salaService.getAllSale()).thenReturn(List.of());

        mockMvc.perform(get("/admin/seanse/1/edytuj"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seans-form"))
                .andExpect(model().attributeExists("seansForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateSeans() throws Exception {
        String dataStr = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES).toString();
        mockMvc.perform(post("/admin/seanse/1")
                        .param("filmId", "1")
                        .param("salaId", "1")
                        .param("dataGodzina", dataStr)
                        .param("cenaNormalny", "25.0")
                        .param("cenaUlgowy", "20.0")
                        .param("cenaRodzinny", "60.0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/seanse"));

        verify(seansService).updateSeans(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteSeans() throws Exception {
        mockMvc.perform(post("/admin/seanse/1/usun")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/seanse"));

        verify(seansService).deleteSeans(1L);
    }
}
