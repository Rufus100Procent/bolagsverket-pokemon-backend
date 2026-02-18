package se.bolagsverket.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.service.AbilityService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class AbilityControllerTest {

    RestTestClient client;
    AbilityService abilityService;

    @BeforeEach
    void setup() {
        abilityService = Mockito.mock(AbilityService.class);
        client = RestTestClient.bindToController(new AbilityController(abilityService)).build();
    }

    @Test
    void getAbilities_withDefaultParams_returnsAbilities() {
        Ability overgrow = createAbility(1L, "Overgrow");
        Ability blaze = createAbility(2L, "Blaze");

        PaginationDto<Ability> expected = new PaginationDto<>(
                List.of(overgrow, blaze), 0, 20, 2, 1, true
        );

        when(abilityService.getAbilities(0, 20, "name", "asc"))
                .thenReturn(expected);

        PaginationDto<Ability> result = client.get()
                .uri("/api/v0/ability")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PaginationDto<Ability>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Overgrow", result.getContent().get(0).getName());
        assertEquals("Blaze", result.getContent().get(1).getName());
    }

    @Test
    void getAbilities_withCustomParams_passesParamsToService() {
        PaginationDto<Ability> expected = new PaginationDto<>(
                List.of(), 1, 10, 0, 0, true
        );

        when(abilityService.getAbilities(1, 10, "id", "desc"))
                .thenReturn(expected);

        client.get()
                .uri("/api/v0/ability?page=1&size=10&sort=id&order=desc")
                .exchange()
                .expectStatus().isOk();

        verify(abilityService).getAbilities(1, 10, "id", "desc");
    }

    @Test
    void getAbilities_withPagination_returnsCorrectPage() {
        Ability ability = createAbility(1L, "Swift");

        PaginationDto<Ability> expected = new PaginationDto<>(
                List.of(ability), 2, 5, 15, 3, false
        );

        when(abilityService.getAbilities(2, 5, "name", "asc"))
                .thenReturn(expected);

        PaginationDto<Ability> result = client.get()
                .uri("/api/v0/ability?page=2&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PaginationDto<Ability>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(15, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isLast());
    }

    //update
    @Test
    void updateAbility_withValidId_returnsUpdatedAbility() {
        Ability updated = createAbility(1L, "Chlorophyll");

        when(abilityService.updateAbility(1L, "Chlorophyll"))
                .thenReturn(updated);

        Ability result = client.put()
                .uri("/api/v0/ability/1?name=Chlorophyll")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ability.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Chlorophyll", result.getName());

        verify(abilityService).updateAbility(1L, "Chlorophyll");
    }

    private Ability createAbility(Long id, String name) {
        Ability ability = new Ability();
        ability.setId(id);
        ability.setName(name);
        return ability;
    }
}