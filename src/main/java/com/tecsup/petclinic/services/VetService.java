package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;

import java.util.List;
import java.util.Optional;

public interface VetService {

    List<VetDTO> findAll();

    Optional<VetDTO> findById(Integer id);
}
