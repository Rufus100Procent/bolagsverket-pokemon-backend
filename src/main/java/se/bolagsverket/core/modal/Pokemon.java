package se.bolagsverket.core.modal;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuppressWarnings("unused")
@Table(name = "pokemon")
public class Pokemon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer height;

    private Integer weight;

    @Column(name = "base_experience")
    private Integer baseExperience;

    @ManyToMany
    @JoinTable(
            name = "pokemon_type",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<Type> types = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "pokemon_ability",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    private Set<Ability> abilities = new HashSet<>();


    public Pokemon() {}

    public Pokemon(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void setHeight(Integer height) { this.height = height; }

    public void setWeight(Integer weight) { this.weight = weight; }

    public void setBaseExperience(Integer baseExperience) { this.baseExperience = baseExperience; }

    public void setTypes(Set<Type> types) { this.types = types; }

    public Set<Type> getTypes() {
        return types;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getBaseExperience() {
        return baseExperience;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(Set<Ability> abilities) { this.abilities = abilities; }
}