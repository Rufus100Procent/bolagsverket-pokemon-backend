package se.bolagsverket.core.service;

import org.springframework.stereotype.Service;
import se.bolagsverket.core.dto.*;
import se.bolagsverket.core.repo.PokemonRepository;

import java.util.List;

@Service
public class PokemonService {

    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public List<PokemonDto> getAllPokemonsName() {
        return pokemonRepository.findAllNamesAndIds();
    }

}