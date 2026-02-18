package se.bolagsverket.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.bolagsverket.core.modal.Type;
import se.bolagsverket.core.repo.TypeRepository;
import se.bolagsverket.error.ErrorType;
import se.bolagsverket.error.PokemonException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TypeService {

    private static final Logger log = LoggerFactory.getLogger(TypeService.class);
    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public Type findTypeById(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new PokemonException(ErrorType.NOT_FOUND,
                        "Type with id " + id + " not found"));
    }

    public Set<Type> resolveTypes(Set<Long> typeIds) {
        if (typeIds == null || typeIds.isEmpty()) return new HashSet<>();
        return typeIds.stream()
                .map(this::findTypeById)
                .collect(Collectors.toCollection(HashSet::new));
    }
}