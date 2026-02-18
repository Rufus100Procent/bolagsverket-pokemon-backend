package se.bolagsverket.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.bolagsverket.core.dto.*;
import se.bolagsverket.core.service.PokemonService;

import java.util.UUID;

import static se.bolagsverket.security.utils.JwtUtil.extractUserId;


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

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDetailsDto> getPokemonById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = extractUserId(jwt);
        return ResponseEntity.ok(pokemonService.getPokemonById(id, userId));
    }

    @PostMapping
    public ResponseEntity<PokemonDetailsDto> createPokemon(
            @Valid @RequestBody CreatePokemonRequest request,
                @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pokemonService.createPokemon(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PokemonDetailsDto> updatePokemon(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePokemonRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return ResponseEntity.ok(pokemonService.updatePokemon(id, request,userId));
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long id,
            @RequestParam boolean value,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = extractUserId(jwt);
        pokemonService.toggleFavorite(id, userId, value);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        pokemonService.deletePokemon(id);
        return ResponseEntity.noContent().build();
    }

}