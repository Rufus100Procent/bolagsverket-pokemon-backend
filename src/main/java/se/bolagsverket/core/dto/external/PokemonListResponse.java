package se.bolagsverket.core.dto.external;

import jakarta.validation.constraints.Size;

import java.util.List;

@SuppressWarnings("unused")
public class PokemonListResponse {

    private List<PokemonInfo> results;

    public List<PokemonInfo> getResults() { return results; }

    public static class PokemonInfo {

        @Size(max = 100)
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}