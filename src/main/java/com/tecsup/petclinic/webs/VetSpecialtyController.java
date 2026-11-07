
package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.services.VetSpecialtyService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class VetSpecialtyController {

    private final VetSpecialtyService service;

    public VetSpecialtyController(VetSpecialtyService service) {
        this.service = service;
    }

    public record AssignmentRequest(
            Integer vetId, Integer specialtyId) {}

    public record ExperienceRequest(
            Integer yearsExperience) {}

    public record CertificationRequest(
            LocalDate certificationDate) {}

    // 1. Consultar especialidades de un veterinario
    @GetMapping("/vets/{vetId}/specialties")
    public ResponseEntity<?> findSpecialtiesByVet(
            @PathVariable int vetId) {

        if (!service.vetExists(vetId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                service.findSpecialtiesByVet(vetId));
    }

    // 2. Consultar veterinarios por especialidad
    @GetMapping("/specialties/{specId}/vets")
    public ResponseEntity<?> findVetsBySpecialty(
            @PathVariable int specId,
            @RequestParam(defaultValue = "0")
            int minExperience) {

        if (!service.specialtyExists(specId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                service.findVetsBySpecialty(
                        specId, minExperience));
    }

    // 3. Asignar una especialidad
    @PostMapping("/vet-specialties")
    public ResponseEntity<?> assign(
            @RequestBody AssignmentRequest request) {

        if (request.vetId() == null
                || request.specialtyId() == null) {
            return ResponseEntity.badRequest().build();
        }

        int vetId = request.vetId();
        int specId = request.specialtyId();

        if (!service.vetExists(vetId)
                || !service.specialtyExists(specId)) {
            return ResponseEntity.notFound().build();
        }

        if (service.assignmentExists(vetId, specId)) {
            return ResponseEntity.status(
                    HttpStatus.CONFLICT).build();
        }

        service.assign(vetId, specId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.findAssignment(
                        vetId, specId).orElseThrow());
    }

    // 4. Eliminar una especialidad asignada
    @DeleteMapping("/vet-specialties/{vetId}/{specId}")
    public ResponseEntity<?> remove(
            @PathVariable int vetId,
            @PathVariable int specId) {

        if (service.remove(vetId, specId) == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    // 5. Establecer la especialidad principal
    @PutMapping(
            "/vet-specialties/{vetId}/{specId}/primary")
    public ResponseEntity<?> setPrimary(
            @PathVariable int vetId,
            @PathVariable int specId) {

        if (!service.setPrimary(vetId, specId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                service.findAssignment(
                        vetId, specId).orElseThrow());
    }

    // 6. Modificar años de experiencia
    @PutMapping(
            "/vet-specialties/{vetId}/{specId}/experience")
    public ResponseEntity<?> updateExperience(
            @PathVariable int vetId,
            @PathVariable int specId,
            @RequestBody ExperienceRequest request) {

        if (request.yearsExperience() == null
                || request.yearsExperience() < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (service.updateExperience(
                vetId, specId,
                request.yearsExperience()) == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                service.findAssignment(
                        vetId, specId).orElseThrow());
    }

    // 7. Modificar fecha de certificacion
    @PutMapping(
            "/vet-specialties/{vetId}/{specId}/certification")
    public ResponseEntity<?> updateCertification(
            @PathVariable int vetId,
            @PathVariable int specId,
            @RequestBody CertificationRequest request) {

        if (request.certificationDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (service.updateCertification(
                vetId, specId,
                request.certificationDate()) == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                service.findAssignment(
                        vetId, specId).orElseThrow());
    }
}
