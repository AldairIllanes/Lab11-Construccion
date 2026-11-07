package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.TypeDTO;
import com.tecsup.petclinic.services.TypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TypesController {

    private final TypeService typeService;

    public TypesController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/types")
    public ResponseEntity<List<TypeDTO>> findAll() {
        return ResponseEntity.ok(typeService.findAll());
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<TypeDTO> findById(@PathVariable Integer id) {
        return typeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
