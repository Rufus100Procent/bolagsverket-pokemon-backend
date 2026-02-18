package se.bolagsverket.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.dto.PokemonDto;
import se.bolagsverket.core.service.PokemonService;

@RestController
@RequestMapping("/api/v0/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public ResponseEntity<PaginationDto<PokemonDto>> getAllPokemonNames(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(pokemonService.getAllPokemonNames(type, sort, order, page, size));
    }

}