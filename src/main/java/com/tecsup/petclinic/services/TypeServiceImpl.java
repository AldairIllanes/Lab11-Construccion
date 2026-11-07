package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.TypeDTO;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.repositories.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;

    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public List<TypeDTO> findAll() {
        return typeRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<TypeDTO> findById(Integer id) {
        return typeRepository.findById(id).map(this::toDTO);
    }

    private TypeDTO toDTO(PetType type) {
        return new TypeDTO(type.getId(), type.getName());
    }
}
