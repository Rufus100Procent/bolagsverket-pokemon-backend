package se.bolagsverket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.dto.PokemonDto;
import se.bolagsverket.core.repo.AbilityRepository;
import se.bolagsverket.core.repo.PokemonRepository;
import se.bolagsverket.core.repo.TypeRepository;
import se.bolagsverket.core.service.PokemonService;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PokemonServiceIT extends AbstractPostgresContainer {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private AbilityRepository abilityRepository;

    @BeforeEach
    void setUp() {
        pokemonRepository.deleteAll();
        typeRepository.deleteAll();
        abilityRepository.deleteAll();
    }

    @Test
    void getPokemons_withNoData_returnsEmptyPage() {
        PaginationDto<PokemonDto> result = pokemonService.getAllPokemonNames(null, "name", "asc", 0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

}