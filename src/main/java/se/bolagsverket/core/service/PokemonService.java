package se.bolagsverket.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.bolagsverket.core.dto.*;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.modal.Pokemon;
import se.bolagsverket.core.modal.Type;
import se.bolagsverket.core.repo.PokemonRepository;
import se.bolagsverket.core.utils.PaginationUtils;
import se.bolagsverket.error.ErrorType;
import se.bolagsverket.error.PokemonException;
import se.bolagsverket.error.UserException;
import se.bolagsverket.security.modal.User;
import se.bolagsverket.security.repo.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PokemonService {

    private static final Logger log = LoggerFactory.getLogger(PokemonService.class);

    private final UserRepository userRepository;
    private final PokemonRepository pokemonRepository;
    private final TypeService typeService;
    private final AbilityService abilityService;

    public PokemonService(UserRepository userRepository, PokemonRepository pokemonRepository,
                          TypeService typeService,
                          AbilityService abilityService) {
        this.userRepository = userRepository;
        this.pokemonRepository = pokemonRepository;
        this.typeService = typeService;
        this.abilityService = abilityService;
    }

    public PaginationDto<PokemonDto> getAllPokemonNames(String type, String sort, String order,
                                                 int page, int size) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort, order, "name");

        Page<Pokemon> pokemonPage;
        if (type != null && !type.isBlank()) {
            pokemonPage = pokemonRepository.findByTypeName(type.toLowerCase(), pageable);
        } else {
            pokemonPage = pokemonRepository.findAll(pageable);
        }

        Page<PokemonDto> summaryPage = pokemonPage.map(p ->
                new PokemonDto(p.getId(), p.getName())
        );

        return PaginationDto.from(summaryPage);
    }

    public PokemonDetailsDto getPokemonById(Long id, UUID userId) {
        Pokemon pokemon = findPokemonById(id);
        Set<Long> favoriteIds = loadFavoriteIds(userId);
        return toDetailsDto(pokemon, favoriteIds);
    }

    @Transactional
    public PokemonDetailsDto createPokemon(CreatePokemonRequest request, UUID userId) {
        if (pokemonRepository.existsByName(request.getName())) {
            throw new PokemonException(ErrorType.ALREADY_EXISTS,
                    "Pokemon '" + request.getName() + "' already exists");
        }

        Pokemon pokemon = new Pokemon();
        pokemon.setName(request.getName());
        pokemon.setHeight(request.getHeight());
        pokemon.setWeight(request.getWeight());
        pokemon.setBaseExperience(request.getBaseExperience());
        pokemon.setTypes(typeService.resolveTypes(request.getTypeIds()));
        pokemon.setAbilities(abilityService.resolveAbilities(request.getAbilityIds()));

        Pokemon saved = pokemonRepository.save(pokemon);
        log.info("Pokemon created: id={}, name={}", saved.getId(), saved.getName());

        if (request.isFavorite() && userId != null) {
            toggleFavorite(saved.getId(), userId, true);
        }

        return toDetailsDto(saved, loadFavoriteIds(userId));
    }

    @Transactional
    public PokemonDetailsDto updatePokemon(Long id, UpdatePokemonRequest request, UUID userId) {
        Pokemon pokemon = findPokemonById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            pokemon.setName(request.getName());
        }
        if (request.getHeight() != null) {
            pokemon.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            pokemon.setWeight(request.getWeight());
        }
        if (request.getBaseExperience() != null) {
            pokemon.setBaseExperience(request.getBaseExperience());
        }
        if (request.getTypeIds() != null && !request.getTypeIds().isEmpty()) {
            pokemon.setTypes(typeService.resolveTypes(request.getTypeIds()));
        }
        if (request.getAbilityIds() != null && !request.getAbilityIds().isEmpty()) {
            pokemon.setAbilities(abilityService.resolveAbilities(request.getAbilityIds()));
        }

        Pokemon saved = pokemonRepository.save(pokemon);
        log.info("Pokemon updated: id={}, name={}", saved.getId(), saved.getName());

        if (request.getFavorite() != null && userId != null) {
            toggleFavorite(saved.getId(), userId, request.getFavorite());
        }

        return toDetailsDto(saved, loadFavoriteIds(userId));
    }

    public void toggleFavorite(Long pokemonId, UUID userId, boolean value) {
        Pokemon pokemon = findPokemonById(pokemonId);
        User user = findUserById(userId);

        boolean alreadyFavorited = user.getFavorites().contains(pokemon);
        if (value == alreadyFavorited) {
            throw new PokemonException(ErrorType.INVALID_INPUT,
                    "Pokemon '" + pokemon.getName() + "' is already " + (value ? "in" : "not in") + " favorites");
        }

        if (value) {
            user.addFavorite(pokemon);
        } else {
            user.removeFavorite(pokemon);
        }

        userRepository.save(user);
        log.info("Favorite set to {}: userId={}, pokemonId={}", value, userId, pokemonId);
    }

    @Transactional
    public void deletePokemon(Long id) {
        Pokemon pokemon = findPokemonById(id);
        pokemon.getTypes().clear();
        pokemon.getAbilities().clear();
        pokemonRepository.save(pokemon);
        pokemonRepository.delete(pokemon);
        log.info("Pokemon deleted: id={}", id);
    }

    private Set<Long> loadFavoriteIds(UUID userId) {
        Set<Long> favoriteIds = new HashSet<>();
        if (userId != null) {
            userRepository.findById(userId).ifPresent(user ->
                    user.getFavorites().forEach(p -> favoriteIds.add(p.getId()))
            );
        }
        return favoriteIds;
    }

    private Pokemon findPokemonById(Long id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonException(ErrorType.NOT_FOUND,
                        "Pokemon with id " + id + " not found"));
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorType.NOT_FOUND, "User not found"));
    }

    private PokemonDetailsDto toDetailsDto(Pokemon pokemon, Set<Long> favoriteIds) {
        PokemonDetailsDto dto = new PokemonDetailsDto();
        dto.setId(pokemon.getId());
        dto.setName(pokemon.getName());
        dto.setHeight(pokemon.getHeight());
        dto.setWeight(pokemon.getWeight());
        dto.setBaseExperience(pokemon.getBaseExperience());
        dto.setTypes(pokemon.getTypes().stream().map(Type::getName).sorted().toList());
        dto.setAbilities(pokemon.getAbilities().stream().map(Ability::getName).sorted().toList());
        dto.setFavorite(favoriteIds.contains(pokemon.getId()));
        return dto;
    }

}