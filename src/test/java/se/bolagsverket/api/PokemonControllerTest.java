package se.bolagsverket.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.dto.PokemonDto;
import se.bolagsverket.core.service.PokemonService;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class PokemonControllerTest {

    RestTestClient client;
    PokemonService pokemonService;

    @BeforeEach
    void setup() {
        pokemonService = Mockito.mock(PokemonService.class);
        client = RestTestClient.bindToController(new PokemonController(pokemonService)).build();
    }

    @Test
    void getAllPokemonNames_withDefaultParams_returnsOkWithContent() {
        PaginationDto<PokemonDto> expected = new PaginationDto<>(
                List.of(new PokemonDto(1L, "Bulbasaur"), new PokemonDto(2L, "Pikachu")),
                0, 20, 2, 1, true
        );

        when(pokemonService.getAllPokemonNames(null, "name", "asc", 0, 20)).thenReturn(expected);

        PaginationDto<PokemonDto> result = client.get()
                .uri("/api/v0/pokemon")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PaginationDto<PokemonDto>>() {})
                .returnResult()
                .getResponseBody();

        assert result != null;
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Bulbasaur", result.getContent().getFirst().getName());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLast());

        verify(pokemonService).getAllPokemonNames(null, "name", "asc", 0, 20);
    }

    @Test
    void getAllPokemonNames_withCustomParams_passesParamsToService() {
        PaginationDto<PokemonDto> expected = new PaginationDto<>(
                List.of(), 1, 10, 0, 0, true
        );

        when(pokemonService.getAllPokemonNames(null, "name", "asc", 1, 10)).thenReturn(expected);

        client.get()
                .uri("/api/v0/pokemon?page=1&size=10")
                .exchange()
                .expectStatus().isOk();

        verify(pokemonService).getAllPokemonNames(null, "name", "asc", 1, 10);
    }

    @Test
    void getAllPokemonNames_withTypeFilter_passesTypeToService() {
        PaginationDto<PokemonDto> expected = new PaginationDto<>(
                List.of(new PokemonDto(1L, "Charmander")), 0, 20, 1, 1, true
        );

        when(pokemonService.getAllPokemonNames("fire", "name", "asc", 0, 20)).thenReturn(expected);

        PaginationDto<PokemonDto> result = client.get()
                .uri("/api/v0/pokemon?type=fire")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PaginationDto<PokemonDto>>() {})
                .returnResult()
                .getResponseBody();

        assert result != null;
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Charmander", result.getContent().getFirst().getName());

        verify(pokemonService).getAllPokemonNames("fire", "name", "asc", 0, 20);
    }
}