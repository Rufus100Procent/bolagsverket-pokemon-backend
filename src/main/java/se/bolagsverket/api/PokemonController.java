package se.bolagsverket.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.bolagsverket.core.dto.PokemonDto;
import se.bolagsverket.core.service.PokemonService;

import java.util.List;

@RestController
@RequestMapping("/api/v0/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public ResponseEntity<List<PokemonDto>> getPokemons() {
        return ResponseEntity.ok(pokemonService.getAllPokemonsName());
    }

}