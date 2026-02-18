package se.bolagsverket.core.dto;


import jakarta.validation.constraints.*;

import java.util.Set;

@SuppressWarnings("unused")
public class CreatePokemonRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be a positive number")
    private Integer height;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number")
    private Integer weight;

    @NotNull(message = "Base experience is required")
    private Integer baseExperience;

    @NotNull(message = "At least one type is required")
    @NotEmpty(message = "At least one type is required")
    private Set<Long> typeIds;

    @NotNull(message = "At least one ability is required")
    @NotEmpty(message = "At least one ability is required")
    private Set<Long> abilityIds;

    private boolean favorite;

    public CreatePokemonRequest() {
    }

    public CreatePokemonRequest(String name, Integer height, Integer weight, Integer baseExperience) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.baseExperience = baseExperience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getBaseExperience() {
        return baseExperience;
    }

    public Set<Long> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(Set<Long> typeIds) {
        this.typeIds = typeIds;
    }

    public Set<Long> getAbilityIds() {
        return abilityIds;
    }

    public void setAbilityIds(Set<Long> abilityIds) {
        this.abilityIds = abilityIds;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setBaseExperience(Integer baseExperience) {
        this.baseExperience = baseExperience;
    }
}
