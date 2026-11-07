package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository repository;

    public SpecialtyServiceImpl(SpecialtyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SpecialtyDTO> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
        Specialty specialty = repository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
        return toDto(specialty);
    }

    @Override
    public SpecialtyDTO create(SpecialtyDTO dto) {
        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());
        specialty.setOffice(dto.getOffice());
        specialty.setOpenHour(dto.getOpenHour());
        specialty.setCloseHour(dto.getCloseHour());
        return toDto(repository.save(specialty));
    }

    @Override
    public void delete(Integer id) throws SpecialtyNotFoundException {
        findById(id);
        repository.deleteById(id);
    }

    private SpecialtyDTO toDto(Specialty s) {
        return SpecialtyDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .office(s.getOffice())
                .openHour(s.getOpenHour())
                .closeHour(s.getCloseHour())
                .build();
    }
}