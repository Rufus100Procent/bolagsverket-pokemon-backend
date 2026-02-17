package se.bolagsverket.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.bolagsverket.core.modal.Ability;

import java.util.Optional;

@Repository
public interface AbilityRepository extends JpaRepository<Ability, Long> {
    Optional<Ability> findByName(String name);
}
