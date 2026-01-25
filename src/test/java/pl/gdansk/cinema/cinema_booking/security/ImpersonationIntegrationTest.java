package pl.gdansk.cinema.cinema_booking.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ImpersonationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldBeAbleToImpersonateUser() throws Exception {
        mockMvc.perform(get("/api/v1/whoami")
                .header("X-Impersonate-User", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("user1"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void userShouldNotBeAbleToImpersonateUser() throws Exception {
        mockMvc.perform(get("/api/v1/whoami")
                .header("X-Impersonate-User", "user2"))
                .andExpect(status().isOk())
                .andExpect(content().string("user1")); // Still user1
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldStillBeAdminWithoutHeader() throws Exception {
        mockMvc.perform(get("/api/v1/whoami"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void swaggerShouldContainImpersonateHeader() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("X-Impersonate-User")));
    }
}
