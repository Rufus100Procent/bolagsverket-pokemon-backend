package se.bolagsverket.api.external;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import se.bolagsverket.core.dto.external.PokemonDetailResponse;
import se.bolagsverket.core.dto.external.PokemonListResponse;


@HttpExchange(accept = "application/json")
public interface PokemonClient {

    @GetExchange("/pokemon?limit=20")
    PokemonListResponse getall();

    @GetExchange("/pokemon/{nameOrId}")
    PokemonDetailResponse getDetail(@PathVariable String nameOrId);
}