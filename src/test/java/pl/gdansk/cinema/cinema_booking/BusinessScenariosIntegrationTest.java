package pl.gdansk.cinema.cinema_booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.gdansk.cinema.cinema_booking.dto.BiletDto;
import pl.gdansk.cinema.cinema_booking.entity.*;
import pl.gdansk.cinema.cinema_booking.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BusinessScenariosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SeansRepository seansRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private UzytkownikRepository uzytkownikRepository;

    @Autowired
    private RezerwacjaRepository rezerwacjaRepository;

    @Autowired
    private BiletRepository biletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Film testFilm;
    private Seans testSeans;
    private Sala testSala;

    @BeforeEach
    void setUp() {
        testFilm = filmRepository.save(Film.builder()
                .tytul("Scenario Film")
                .czasTrwania(120)
                .build());
        
        testSala = salaRepository.save(Sala.builder().numer(1).build());
        
        testSeans = seansRepository.save(Seans.builder()
                .film(testFilm)
                .sala(testSala)
                .dataGodzina(LocalDateTime.now().plusDays(1))
                .cenaNormalny(25.0)
                .build());
        
        if (uzytkownikRepository.findByUsername("testScenarioUser").isEmpty()) {
            uzytkownikRepository.save(Uzytkownik.builder()
                    .username("testScenarioUser")
                    .haslo("pass")
                    .role(java.util.Set.of("USER"))
                    .build());
        }
    }

    @Test
    @WithMockUser(username = "testScenarioUser")
    void scenario1_FullReservationProcess() throws Exception {
        List<BiletDto> bilety = List.of(BiletDto.builder()
                .rzad(1).miejsce(1).typBiletu("NORMALNY").cena(25.0).build());

        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();

        // 1. Add to cart
        mockMvc.perform(post("/rezerwacja/do-koszyka")
                        .session(session)
                        .param("seansId", testSeans.getId().toString())
                        .content(objectMapper.writeValueAsString(bilety))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());

        // 2. Finalize
        mockMvc.perform(post("/rezerwacja/finalizuj")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("potwierdzenie"))
                .andExpect(model().attributeExists("ticketId"));

        // 3. Verify in DB
        List<Rezerwacja> rezerwacje = rezerwacjaRepository.findByUzytkownikUsername("testScenarioUser");
        assertThat(rezerwacje).hasSize(1);
        assertThat(biletRepository.findBySeansId(testSeans.getId())).hasSize(1);
    }

    @Test
    @WithMockUser(username = "testScenarioUser")
    void scenario2_CannotReserveOccupiedSeat() throws Exception {
        // First reservation
        biletRepository.save(Bilet.builder()
                .seans(testSeans)
                .rzad(1).miejsce(1)
                .typBiletu(TypBiletu.NORMALNY)
                .build());

        List<BiletDto> bilety = List.of(BiletDto.builder()
                .rzad(1).miejsce(1).typBiletu("NORMALNY").cena(25.0).build());

        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();

        // Try second reservation
        mockMvc.perform(post("/rezerwacja/do-koszyka")
                        .session(session)
                        .param("seansId", testSeans.getId().toString())
                        .content(objectMapper.writeValueAsString(bilety))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/rezerwacja/finalizuj")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("podsumowanie"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void scenario3_AdminManagesFilms() throws Exception {
        // 1. Create film
        String newFilmJson = "{\"tytul\":\"Admin Film\",\"gatunek\":\"Action\",\"wiek\":16,\"czasTrwania\":100}";
        mockMvc.perform(post("/api/v1/filmy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newFilmJson)
                        .with(csrf()))
                .andExpect(status().isCreated());

        Film createdFilm = filmRepository.findAll().stream()
                .filter(f -> f.getTytul().equals("Admin Film"))
                .findFirst().orElseThrow();

        // 2. Update film
        mockMvc.perform(put("/api/v1/filmy/" + createdFilm.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tytul\":\"Updated Admin Film\",\"gatunek\":\"Action\",\"wiek\":16,\"czasTrwania\":110}")
                        .with(csrf()))
                .andExpect(status().isOk());

        // 3. Delete film
        mockMvc.perform(delete("/api/v1/filmy/" + createdFilm.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(filmRepository.existsById(createdFilm.getId())).isFalse();
    }

    @Test
    void scenario4_BrowseSchedule() throws Exception {
        String tomorrow = java.time.LocalDate.now().plusDays(1).toString();
        mockMvc.perform(get("/").param("date", tomorrow))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("filmy"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Scenario Film")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void scenario5_DownloadOccupancyReport() throws Exception {
        // 1. First, perform a reservation using its own session
        List<BiletDto> bilety = List.of(BiletDto.builder()
                .rzad(2).miejsce(2).typBiletu("NORMALNY").cena(25.0).build());

        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();

        mockMvc.perform(post("/rezerwacja/do-koszyka")
                        .session(session)
                        .param("seansId", testSeans.getId().toString())
                        .content(objectMapper.writeValueAsString(bilety))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/rezerwacja/finalizuj")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk());

        // 2. Download report
        mockMvc.perform(get("/api/v1/raporty/oblozenie/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("raport_oblozenia.csv")))
                .andExpect(content().contentType("text/csv"));
    }
}
