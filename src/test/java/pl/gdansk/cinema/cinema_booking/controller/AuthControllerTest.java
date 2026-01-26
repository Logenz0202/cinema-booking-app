package pl.gdansk.cinema.cinema_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.gdansk.cinema.cinema_booking.config.SecurityConfig;
import pl.gdansk.cinema.cinema_booking.dto.RejestracjaDto;
import pl.gdansk.cinema.cinema_booking.service.UzytkownikService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UzytkownikService uzytkownikService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.service.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private pl.gdansk.cinema.cinema_booking.security.ImpersonationFilter impersonationFilter;

    @org.junit.jupiter.api.BeforeEach
    void setup() throws jakarta.servlet.ServletException, java.io.IOException {
        doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletRequest request = invocation.getArgument(0);
            jakarta.servlet.http.HttpServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(impersonationFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldReturnRegistrationForm() throws Exception {
        mockMvc.perform(get("/rejestracja"))
                .andExpect(status().isOk())
                .andExpect(view().name("rejestracja"))
                .andExpect(model().attributeExists("uzytkownik"));
    }

    @Test
    void shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        mockMvc.perform(post("/rejestracja")
                        .param("username", "newUser")
                        .param("haslo", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        verify(uzytkownikService, times(1)).zarejestruj(any(RejestracjaDto.class));
    }

    @Test
    void shouldReturnRegistrationFormWithErrorWhenServiceThrowsException() throws Exception {
        doThrow(new RuntimeException("Użytkownik już istnieje"))
                .when(uzytkownikService).zarejestruj(any(RejestracjaDto.class));

        mockMvc.perform(post("/rejestracja")
                        .param("username", "existingUser")
                        .param("haslo", "password")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("rejestracja"))
                .andExpect(model().attribute("error", "Użytkownik już istnieje"));
    }
}
