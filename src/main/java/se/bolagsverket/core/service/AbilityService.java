package se.bolagsverket.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.repo.AbilityRepository;
import se.bolagsverket.error.ErrorType;
import se.bolagsverket.error.PokemonException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AbilityService {

    private static final Logger log = LoggerFactory.getLogger(AbilityService.class);
    private final AbilityRepository abilityRepository;

    public AbilityService(AbilityRepository abilityRepository) {
        this.abilityRepository = abilityRepository;
    }


    public Ability findAbilityById(Long id) {
        return abilityRepository.findById(id)
                .orElseThrow(() -> new PokemonException(ErrorType.NOT_FOUND,
                        "Ability with id " + id + " not found"));
    }

    public Set<Ability> resolveAbilities(Set<Long> abilityIds) {
        if (abilityIds == null || abilityIds.isEmpty()) return new HashSet<>();
        return abilityIds.stream()
                .map(this::findAbilityById)
                .collect(Collectors.toCollection(HashSet::new));
    }
}