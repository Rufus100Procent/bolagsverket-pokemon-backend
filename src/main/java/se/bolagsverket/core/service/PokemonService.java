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

@Service
public class PokemonService {

    private static final Logger log = LoggerFactory.getLogger(PokemonService.class);

    private final PokemonRepository pokemonRepository;
    private final TypeService typeService;
    private final AbilityService abilityService;

    public PokemonService(PokemonRepository pokemonRepository,
                          TypeService typeService,
                          AbilityService abilityService) {
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

    public PokemonDetailsDto getPokemonById(Long id) {
        Pokemon pokemon = findPokemonById(id);
        return toDto(pokemon);
    }

    @Transactional
    public PokemonDetailsDto createPokemon(CreatePokemonRequest request) {
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

        Pokemon savedPokemon = pokemonRepository.save(pokemon);
        log.info("Pokemon created: id={}, name={}", savedPokemon.getId(), savedPokemon.getName());
        return toDto(savedPokemon);
    }

    @Transactional
    public PokemonDetailsDto updatePokemon(Long id, UpdatePokemonRequest request) {
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

        Pokemon savedPokemon = pokemonRepository.save(pokemon);
        log.info("Pokemon updated: id={}, name={}", savedPokemon.getId(), savedPokemon.getName());
        return toDto(savedPokemon);
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

    private Pokemon findPokemonById(Long id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonException(ErrorType.NOT_FOUND,
                        "Pokemon with id " + id + " not found"));
    }

    private PokemonDetailsDto toDto(Pokemon pokemon) {
        PokemonDetailsDto dto = new PokemonDetailsDto();
        dto.setId(pokemon.getId());
        dto.setName(pokemon.getName());
        dto.setHeight(pokemon.getHeight());
        dto.setWeight(pokemon.getWeight());
        dto.setBaseExperience(pokemon.getBaseExperience());
        dto.setTypes(pokemon.getTypes().stream().map(Type::getName).sorted().toList());
        dto.setAbilities(pokemon.getAbilities().stream().map(Ability::getName).sorted().toList());
        return dto;
    }

}