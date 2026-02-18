package se.bolagsverket.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.bolagsverket.db.AbstractPostgresContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PokemonControllerIT extends AbstractPostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deletePokemon_withoutToken_returns401() throws Exception {
        mockMvc.perform(delete("/api/v0/pokemon/1"))
                .andExpect(status().isUnauthorized());
    }
}