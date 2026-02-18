package se.bolagsverket.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.bolagsverket.core.dto.*;
import se.bolagsverket.core.modal.Pokemon;
import se.bolagsverket.core.repo.PokemonRepository;
import se.bolagsverket.core.utils.PaginationUtils;

@Service
public class PokemonService {

    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
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

}