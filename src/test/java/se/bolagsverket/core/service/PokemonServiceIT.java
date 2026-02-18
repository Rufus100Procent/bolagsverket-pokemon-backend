package se.bolagsverket.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import se.bolagsverket.core.dto.*;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.modal.Pokemon;
import se.bolagsverket.core.modal.Type;
import se.bolagsverket.core.repo.AbilityRepository;
import se.bolagsverket.core.repo.PokemonRepository;
import se.bolagsverket.core.repo.TypeRepository;
import se.bolagsverket.db.AbstractPostgresContainer;
import se.bolagsverket.error.ErrorType;
import se.bolagsverket.error.PokemonException;
import se.bolagsverket.security.modal.User;
import se.bolagsverket.security.repo.UserRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PokemonServiceIT extends AbstractPostgresContainer {

    @Autowired private PokemonService pokemonService;
    @Autowired private PokemonRepository pokemonRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private AbilityRepository abilityRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        pokemonRepository.deleteAll();
        typeRepository.deleteAll();
        abilityRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void getAllPokemonNames_withData_returnsPaginatedAndSorted() {
        createPokemon("Squirtle");
        createPokemon("Bulbasaur");
        createPokemon("Charmander");

        PaginationDto<PokemonDto> result = pokemonService.getAllPokemonNames(null, "name", "asc", 0, 2);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals("Bulbasaur", result.getContent().getFirst().getName());
        assertFalse(result.isLast());
    }

    @Test
    void getAllPokemonNames_withTypeFilter_returnsOnlyMatching() {
        Type fire = createType("fire");
        Pokemon charmander = createPokemon("Charmander");
        charmander.getTypes().add(fire);
        pokemonRepository.save(charmander);
        createPokemon("Squirtle");

        PaginationDto<PokemonDto> result = pokemonService.getAllPokemonNames("fire", "name", "asc", 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("Charmander", result.getContent().getFirst().getName());
    }

    // byid
    @Test
    void getPokemonById_withValidId_returnsFullDetails() {
        Type fire = createType("fire");
        Ability blaze = createAbility("blaze");
        Pokemon pokemon = createPokemon("Charmander");
        pokemon.getTypes().add(fire);
        pokemon.getAbilities().add(blaze);
        pokemonRepository.save(pokemon);

        User user = createUser("ash");
        user.addFavorite(pokemon);
        userRepository.save(user);

        PokemonDetailsDto result = pokemonService.getPokemonById(pokemon.getId(), user.getId());

        assertEquals("Charmander", result.getName());
        assertTrue(result.getTypes().contains("fire"));
        assertTrue(result.getAbilities().contains("blaze"));
        assertTrue(result.isFavorite());
    }

    @Test
    void getPokemonById_withDifferentUser_returnsFavoriteFalse() {
        Pokemon pokemon = createPokemon("Pikachu");
        User usr = createUser("admin");
        usr.addFavorite(pokemon);
        userRepository.save(usr);

        PokemonDetailsDto result = pokemonService.getPokemonById(pokemon.getId(), createUser("jl").getId());

        assertFalse(result.isFavorite());
    }

    @Test
    void getPokemonById_withInvalidId_throwsNotFound() {
        PokemonException ex = assertThrows(PokemonException.class,
                () -> pokemonService.getPokemonById(999L, null));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    //create
    @Test
    void createPokemon_withValidRequest_savesAndReturnsPokemon() {
        Type fire = createType("fire");
        Ability blaze = createAbility("blaze");
        User user = createUser("ash");

        CreatePokemonRequest request = buildCreateRequest("Charmander");
        request.setTypeIds(Set.of(fire.getId()));
        request.setAbilityIds(Set.of(blaze.getId()));
        request.setFavorite(true);

        PokemonDetailsDto result = pokemonService.createPokemon(request, user.getId());

        assertNotNull(result.getId());
        assertEquals("Charmander", result.getName());
        assertTrue(result.getTypes().contains("fire"));
        assertTrue(result.getAbilities().contains("blaze"));
        assertTrue(result.isFavorite());
    }

    @Test
    void createPokemon_withDuplicateName_throwsAlreadyExists() {
        createPokemon("Pikachu");
        PokemonException ex = assertThrows(PokemonException.class,
                () -> pokemonService.createPokemon(buildCreateRequest("Pikachu"), null));
        assertEquals(ErrorType.ALREADY_EXISTS, ex.getErrorType());
    }

    // update

    @Test
    void updatePokemon_withValidRequest_updatesFields() {
        Type water = createType("water");
        Ability torrent = createAbility("torrent");
        Pokemon pokemon = createPokemon("Pikachu");

        UpdatePokemonRequest request = new UpdatePokemonRequest();
        request.setName("Raichu");
        request.setHeight(8);
        request.setWeight(300);
        request.setBaseExperience(218);
        request.setTypeIds(Set.of(water.getId()));
        request.setAbilityIds(Set.of(torrent.getId()));

        PokemonDetailsDto result = pokemonService.updatePokemon(pokemon.getId(), request, null);

        assertEquals("Raichu", result.getName());
        assertEquals(8, result.getHeight());
        assertEquals(300, result.getWeight());
        assertEquals(218, result.getBaseExperience());
        assertTrue(result.getTypes().contains("water"));
        assertTrue(result.getAbilities().contains("torrent"));
    }

    @Test
    void updatePokemon_withNullFields_doesNotOverwrite() {
        Pokemon pokemon = createPokemon("Pikachu");
        PokemonDetailsDto result = pokemonService.updatePokemon(pokemon.getId(), new UpdatePokemonRequest(), null);
        assertEquals("Pikachu", result.getName());
        assertEquals(10, result.getHeight());
    }

    @Test
    void updatePokemon_withInvalidId_throwsNotFound() {
        PokemonException ex = assertThrows(PokemonException.class,
                () -> pokemonService.updatePokemon(999L, new UpdatePokemonRequest(), null));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    // toggleFavorite

    @Test
    void toggleFavorite_addAndRemove_workCorrectly() {
        Pokemon pokemon = createPokemon("Pikachu");
        User user = createUser("user");

        pokemonService.toggleFavorite(pokemon.getId(), user.getId(), true);
        User afterAdd = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(afterAdd.getFavorites().stream().anyMatch(p -> p.getId().equals(pokemon.getId())));

        pokemonService.toggleFavorite(pokemon.getId(), user.getId(), false);
        User afterRemove = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(afterRemove.getFavorites().stream().noneMatch(p -> p.getId().equals(pokemon.getId())));
    }

    @Test
    void toggleFavorite_sameValueTwice_throwsInvalidInput() {
        Pokemon pokemon = createPokemon("Pikachu");
        User user = createUser("user");
        user.addFavorite(pokemon);
        userRepository.save(user);

        PokemonException ex = assertThrows(PokemonException.class,
                () -> pokemonService.toggleFavorite(pokemon.getId(), user.getId(), true));
        assertEquals(ErrorType.INVALID_INPUT, ex.getErrorType());
    }

    //delete
    @Test
    void deletePokemon_withValidId_removesOnlyThatPokemon() {
        Type fire = createType("fire");
        Ability blaze = createAbility("blaze");

        Pokemon charmander = createPokemon("Charmander");
        Pokemon charmeleon = createPokemon("Charmeleon");
        charmander.getTypes().add(fire);
        charmander.getAbilities().add(blaze);
        charmeleon.getTypes().add(fire);
        charmeleon.getAbilities().add(blaze);
        pokemonRepository.save(charmander);
        pokemonRepository.save(charmeleon);

        pokemonService.deletePokemon(charmander.getId());

        assertFalse(pokemonRepository.existsById(charmander.getId()));
        assertTrue(typeRepository.existsById(fire.getId()));
        assertTrue(abilityRepository.existsById(blaze.getId()));
        assertTrue(pokemonRepository.existsById(charmeleon.getId()));
    }

    @Test
    void deletePokemon_withInvalidId_throwsNotFound() {
        PokemonException ex = assertThrows(PokemonException.class,
                () -> pokemonService.deletePokemon(999L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    private Pokemon createPokemon(String name) {
        Pokemon p = new Pokemon();
        p.setName(name);
        p.setHeight(10);
        p.setWeight(100);
        p.setBaseExperience(50);
        return pokemonRepository.save(p);
    }

    private Type createType(String name) {
        Type newType = new Type();
        newType.setName(name);
        return typeRepository.save(newType);
    }

    private Ability createAbility(String name) {
        Ability newAbility = new Ability();
        newAbility.setName(name);
        return abilityRepository.save(newAbility);
    }

    private User createUser(String username) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setHashPassword("hashed");
        return userRepository.save(newUser);
    }

    private CreatePokemonRequest buildCreateRequest(String name) {
        CreatePokemonRequest newPokemon = new CreatePokemonRequest();
        newPokemon.setName(name);
        newPokemon.setHeight(4);
        newPokemon.setWeight(60);
        newPokemon.setBaseExperience(112);
        return newPokemon;
    }
}