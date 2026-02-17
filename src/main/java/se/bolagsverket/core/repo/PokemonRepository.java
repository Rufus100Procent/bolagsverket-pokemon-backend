package se.bolagsverket.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.bolagsverket.core.dto.PokemonDto;
import se.bolagsverket.core.modal.Pokemon;

import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    @Query("SELECT new se.bolagsverket.core.dto.PokemonDto(p.id, p.name) FROM Pokemon p")
    List<PokemonDto> findAllNamesAndIds();
}