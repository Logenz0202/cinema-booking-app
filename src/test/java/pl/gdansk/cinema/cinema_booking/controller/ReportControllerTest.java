package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.dto.SeansOccupancyReportDto;
import pl.gdansk.cinema.cinema_booking.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsService statisticsService;

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
    void shouldDownloadOccupancyReportCsv() throws Exception {
        // given
        SeansOccupancyReportDto reportItem = SeansOccupancyReportDto.builder()
                .seansId(1L)
                .tytul("Film 1")
                .dataGodzina(LocalDateTime.now())
                .zajeteMiejsca(10L)
                .wszystkieMiejsca(100L)
                .build();
        when(statisticsService.getSeansOccupancyReport()).thenReturn(List.of(reportItem));

        // when & then
        mockMvc.perform(get("/api/v1/raporty/oblozenie/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=raport_oblozenia.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ID;Film;Data;Zajete;Wszystkie")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1;Film 1;")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessToReportForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/raporty/oblozenie/csv"))
                .andExpect(status().isForbidden());
    }
}
