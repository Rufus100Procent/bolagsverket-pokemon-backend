package se.bolagsverket.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.modal.Ability;
import se.bolagsverket.core.service.AbilityService;

@RestController
@RequestMapping("/api/v0/ability")
public class AbilityController {

    private final AbilityService abilityService;

    public AbilityController(AbilityService abilityService) {
        this.abilityService = abilityService;
    }

    @GetMapping
    public ResponseEntity<PaginationDto<Ability>> getAbilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order
    ) {
        return ResponseEntity.ok(abilityService.getAbilities(page, size, sort, order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ability> updateAbility(
            @PathVariable Long id,
            @RequestParam String name) {
        return ResponseEntity.ok(abilityService.updateAbility(id, name));
    }
}
