
package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.services.OwnerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService service;

    public OwnerController(OwnerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Owner>> findAll(
            @RequestParam(required = false) String lastName) {

        return ResponseEntity.ok(service.findAll(lastName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Owner> findById(@PathVariable Long id) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Owner> create(@RequestBody Owner owner) {

        if (!validOwner(owner)) {
            return ResponseEntity.badRequest().build();
        }

        Owner created = service.create(owner);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Owner> update(
            @PathVariable Long id,
            @RequestBody Owner owner) {

        if (!validOwner(owner)) {
            return ResponseEntity.badRequest().build();
        }

        return service.update(id, owner)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    private boolean validOwner(Owner owner) {
        return owner.getFirstName() != null
                && !owner.getFirstName().isBlank()
                && owner.getLastName() != null
                && !owner.getLastName().isBlank();
    }
}
