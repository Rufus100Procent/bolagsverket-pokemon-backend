package se.bolagsverket.core.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.bolagsverket.core.modal.Pokemon;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    @Query("SELECT DISTINCT p FROM Pokemon p JOIN p.types t WHERE t.name = :typeName")
    Page<Pokemon> findByTypeName(@Param("typeName") String typeName, Pageable pageable);

}