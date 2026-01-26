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
import pl.gdansk.cinema.cinema_booking.dto.SeansDto;
import pl.gdansk.cinema.cinema_booking.service.SeansService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeansController.class)
@Import(SecurityConfig.class)
class SeansControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SeansService seansService;

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
    void shouldGetAllSeanse() throws Exception {
        SeansDto seans = SeansDto.builder().id(1L).filmTytul("Film").build();
        when(seansService.getAllSeanse()).thenReturn(List.of(seans));

        mockMvc.perform(get("/api/v1/seanse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].filmTytul").value("Film"));
    }

    @Test
    void shouldGetSeansById() throws Exception {
        SeansDto seans = SeansDto.builder().id(1L).filmTytul("Film").build();
        when(seansService.getSeansById(1L)).thenReturn(seans);

        mockMvc.perform(get("/api/v1/seanse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filmTytul").value("Film"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateSeans() throws Exception {
        SeansDto seansDto = SeansDto.builder()
                .filmId(1L)
                .salaId(1L)
                .dataGodzina(LocalDateTime.now().plusDays(1))
                .cenaNormalny(25.0)
                .cenaUlgowy(20.0)
                .cenaRodzinny(60.0)
                .build();
        SeansDto savedSeans = SeansDto.builder().id(1L).filmId(1L).build();
        when(seansService.createSeans(any(SeansDto.class))).thenReturn(savedSeans);

        mockMvc.perform(post("/api/v1/seanse")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seansDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateSeans() throws Exception {
        SeansDto seansDto = SeansDto.builder()
                .filmId(1L)
                .salaId(1L)
                .dataGodzina(LocalDateTime.now().plusDays(1))
                .cenaNormalny(30.0)
                .cenaUlgowy(25.0)
                .cenaRodzinny(70.0)
                .build();
        SeansDto updatedSeans = SeansDto.builder().id(1L).cenaNormalny(30.0).build();
        when(seansService.updateSeans(eq(1L), any(SeansDto.class))).thenReturn(updatedSeans);

        mockMvc.perform(put("/api/v1/seanse/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seansDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cenaNormalny").value(30.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteSeans() throws Exception {
        mockMvc.perform(delete("/api/v1/seanse/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(seansService).deleteSeans(1L);
    }
}
