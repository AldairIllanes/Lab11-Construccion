package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.repositories.VetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    public List<VetDTO> findAll() {
        return vetRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<VetDTO> findById(Integer id) {
        return vetRepository.findById(id).map(this::toDTO);
    }

    private VetDTO toDTO(Vet vet) {
        return new VetDTO(vet.getId(), vet.getFirstName(), vet.getLastName());
    }
}
