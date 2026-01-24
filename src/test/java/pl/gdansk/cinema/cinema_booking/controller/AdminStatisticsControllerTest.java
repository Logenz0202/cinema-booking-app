package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.service.StatisticsService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminStatisticsController.class)
@Import(SecurityConfig.class)
class AdminStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowStatisticsPage() throws Exception {
        when(statisticsService.getSalesStatistics()).thenReturn(List.of());
        when(statisticsService.getSeansOccupancyReport()).thenReturn(List.of());

        mockMvc.perform(get("/admin/statystyki"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/statystyki"))
                .andExpect(model().attributeExists("salesStats"))
                .andExpect(model().attributeExists("occupancyStats"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessForNonAdmin() throws Exception {
        mockMvc.perform(get("/admin/statystyki"))
                .andExpect(status().isForbidden());
    }
}
