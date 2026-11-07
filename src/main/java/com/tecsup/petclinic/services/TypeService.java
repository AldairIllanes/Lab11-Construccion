package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.TypeDTO;

import java.util.List;
import java.util.Optional;

public interface TypeService {

    List<TypeDTO> findAll();

    Optional<TypeDTO> findById(Integer id);
}
