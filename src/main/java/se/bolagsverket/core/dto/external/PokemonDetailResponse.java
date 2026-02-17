package se.bolagsverket.core.dto.external;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.List;

@SuppressWarnings("unused")
public class PokemonDetailResponse {

    @Size(max = 100)
    private String name;

    private int height;
    private int weight;

    @JsonProperty("base_experience")
    private Integer baseExperience;

    private List<TypeSlot> types;
    private List<AbilitySlot> abilities;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public Integer getBaseExperience() { return baseExperience; }
    public void setBaseExperience(Integer baseExperience) { this.baseExperience = baseExperience; }

    public List<TypeSlot> getTypes() { return types; }
    public void setTypes(List<TypeSlot> types) { this.types = types; }

    public List<AbilitySlot> getAbilities() { return abilities; }
    public void setAbilities(List<AbilitySlot> abilities) { this.abilities = abilities; }

    public static class TypeSlot {
        private TypeName type;

        public void setType(TypeName type) { this.type = type; }
        public String getName() { return type.getName(); }

        public static class TypeName {
            @Size(max = 50)
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }

    public static class AbilitySlot {
        private AbilityName ability;

        public void setAbility(AbilityName ability) { this.ability = ability; }
        public String getName() { return ability.getName(); }

        public static class AbilityName {
            @Size(max = 50)
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}