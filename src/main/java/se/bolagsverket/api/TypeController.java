package se.bolagsverket.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.bolagsverket.core.dto.PaginationDto;
import se.bolagsverket.core.modal.Type;
import se.bolagsverket.core.service.TypeService;

@RestController
@RequestMapping("/api/v0/type")
public class TypeController {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping
    public ResponseEntity<PaginationDto<Type>> getTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order
    ) {
        return ResponseEntity.ok(typeService.getTypes(page, size, sort, order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Type> updateType(
            @PathVariable Long id,
            @RequestParam String  name) {
        return ResponseEntity.ok(typeService.updateType(id, name));
    }
}