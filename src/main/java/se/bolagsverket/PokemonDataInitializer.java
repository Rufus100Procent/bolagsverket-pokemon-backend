package se.bolagsverket;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.bolagsverket.api.external.PokemonClient;
import se.bolagsverket.core.dto.external.PokemonDetailResponse;
import se.bolagsverket.core.dto.external.PokemonListResponse;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.modal.Pokemon;
import se.bolagsverket.core.modal.Type;
import se.bolagsverket.core.repo.AbilityRepository;
import se.bolagsverket.core.repo.PokemonRepository;
import se.bolagsverket.core.repo.TypeRepository;

import java.util.HashSet;
import java.util.Set;


@Service
@Profile("!test")
public class PokemonDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PokemonDataInitializer.class);

    private final PokemonClient pokemonClient;
    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private final AbilityRepository abilityRepository;

    @Autowired
    public PokemonDataInitializer(
            PokemonClient pokemonClient,
            PokemonRepository pokemonRepository,
            TypeRepository typeRepository,
            AbilityRepository abilityRepository
    ) {
        this.pokemonClient = pokemonClient;
        this.pokemonRepository = pokemonRepository;
        this.typeRepository = typeRepository;
        this.abilityRepository = abilityRepository;
    }

    @Override
    public void run(String @NonNull ... args) {
        if (pokemonRepository.count() == 0) {
            importPokemonData();
        }
    }

    private void importPokemonData() {
        try {
            PokemonListResponse listResponse = pokemonClient.getall();
            logger.info("Fetched {} Pokemon names from PokeAPI", listResponse.getResults().size());

            int successCount = 0;

            for (PokemonListResponse.PokemonInfo info : listResponse.getResults()) {
                if (importSinglePokemon(info)) {
                    successCount++;
                    if (successCount % 100 == 0) {
                        logger.info("Imported {}/{} pokemon...", successCount, listResponse.getResults().size());
                    }
                }
            }

            logger.info("Successfully imported {}/{} Pokemon", successCount, listResponse.getResults().size());

        } catch (Exception e) {
            logger.error("Error importing Pokemon data", e);
        }
    }

    private boolean importSinglePokemon(PokemonListResponse.PokemonInfo info) {
        try {
            PokemonDetailResponse detail = pokemonClient.getDetail(info.getName());

            Set<Type> types = resolveTypes(detail);
            Set<Ability> abilities = resolveAbilities(detail);

            Pokemon pokemon = new Pokemon();
            pokemon.setName(detail.getName());
            pokemon.setHeight(detail.getHeight());
            pokemon.setWeight(detail.getWeight());
            pokemon.setBaseExperience(detail.getBaseExperience());
            pokemon.setTypes(types);
            pokemon.setAbilities(abilities);

            pokemonRepository.save(pokemon);
            return true;

        } catch (Exception e) {
            logger.warn("Failed to import pokemon: {} — {}", info.getName(), e.getMessage());
            return false;
        }
    }

    private Set<Type> resolveTypes(PokemonDetailResponse detail) {
        Set<Type> types = new HashSet<>();

        for (PokemonDetailResponse.TypeSlot typeSlot : detail.getTypes()) {
            String typeName = typeSlot.getName();

            Type type = typeRepository.findByName(typeName)
                    .orElseGet(() -> typeRepository.save(new Type(typeName)));

            types.add(type);
        }

        return types;
    }

    private Set<Ability> resolveAbilities(PokemonDetailResponse detail) {
        Set<Ability> abilities = new HashSet<>();

        for (PokemonDetailResponse.AbilitySlot abilitySlot : detail.getAbilities()) {
            String abilityName = abilitySlot.getName();

            Ability ability = abilityRepository.findByName(abilityName)
                    .orElseGet(() -> abilityRepository.save(new Ability(abilityName)));

            abilities.add(ability);
        }

        return abilities;
    }

}