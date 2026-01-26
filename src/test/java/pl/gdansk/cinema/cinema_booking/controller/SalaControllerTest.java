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
import pl.gdansk.cinema.cinema_booking.dto.SalaDto;
import pl.gdansk.cinema.cinema_booking.service.SalaService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalaController.class)
@Import(SecurityConfig.class)
class SalaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldGetAllSale() throws Exception {
        SalaDto sala = SalaDto.builder().id(1L).numer(1).build();
        when(salaService.getAllSale()).thenReturn(List.of(sala));

        mockMvc.perform(get("/api/v1/sale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].numer").value(1));
    }

    @Test
    void shouldGetSalaById() throws Exception {
        SalaDto sala = SalaDto.builder().id(1L).numer(1).build();
        when(salaService.getSalaById(1L)).thenReturn(sala);

        mockMvc.perform(get("/api/v1/sale/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numer").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateSala() throws Exception {
        SalaDto salaDto = SalaDto.builder().numer(2).rzedy(10).miejscaWRzedzie(10).build();
        SalaDto savedSala = SalaDto.builder().id(2L).numer(2).build();
        when(salaService.createSala(any(SalaDto.class))).thenReturn(savedSala);

        mockMvc.perform(post("/api/v1/sale")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateSala() throws Exception {
        SalaDto salaDto = SalaDto.builder().numer(2).rzedy(12).miejscaWRzedzie(12).build();
        SalaDto updatedSala = SalaDto.builder().id(1L).numer(2).build();
        when(salaService.updateSala(eq(1L), any(SalaDto.class))).thenReturn(updatedSala);

        mockMvc.perform(put("/api/v1/sale/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numer").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteSala() throws Exception {
        mockMvc.perform(delete("/api/v1/sale/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(salaService).deleteSala(1L);
    }
}
