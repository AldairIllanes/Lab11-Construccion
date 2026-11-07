package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Pet;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.repositories.PetRepository;
import com.tecsup.petclinic.repositories.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PetRepository petRepository;

    public VisitServiceImpl(VisitRepository visitRepository, PetRepository petRepository) {
        this.visitRepository = visitRepository;
        this.petRepository = petRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitDTO> findAll() {
        return visitRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VisitDTO findById(Long id) throws VisitNotFoundException {
        return toDTO(visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitDTO> findByPetId(Integer petId) {
        return visitRepository.findByPet_Id(petId).stream().map(this::toDTO).toList();
    }

    @Override
    public VisitDTO create(VisitDTO visitDTO) {
        Visit visit = new Visit();
        apply(visit, visitDTO);
        return toDTO(visitRepository.save(visit));
    }

    @Override
    public VisitDTO update(Long id, VisitDTO visitDTO) throws VisitNotFoundException {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found: " + id));
        apply(visit, visitDTO);
        return toDTO(visitRepository.save(visit));
    }

    @Override
    public void delete(Long id) throws VisitNotFoundException {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found: " + id));
        visitRepository.delete(visit);
    }

    private void apply(Visit visit, VisitDTO dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));
        visit.setPet(pet);
        visit.setVetId(dto.getVetId());
        visit.setVisitDate(dto.getVisitDate());
        visit.setDescription(dto.getDescription());
        visit.setCost(dto.getCost());
    }

    private VisitDTO toDTO(Visit visit) {
        return VisitDTO.builder()
                .id(visit.getId())
                .petId(visit.getPet().getId())
                .vetId(visit.getVetId())
                .visitDate(visit.getVisitDate())
                .description(visit.getDescription())
                .cost(visit.getCost())
                .build();
    }
}
