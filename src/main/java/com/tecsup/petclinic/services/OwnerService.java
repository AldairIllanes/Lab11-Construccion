
package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.repositories.OwnerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {

    private final OwnerRepository repository;

    public OwnerService(OwnerRepository repository) {
        this.repository = repository;
    }

    public List<Owner> findAll(String lastName) {
        if (lastName != null && !lastName.isBlank()) {
            return repository.findByLastName(lastName);
        }
        return repository.findAll(Sort.by("id"));
    }

    public Optional<Owner> findById(Long id) {
        return repository.findById(id);
    }

    public Owner create(Owner owner) {
        owner.setId(null);
        return repository.save(owner);
    }

    public Optional<Owner> update(Long id, Owner data) {
        return repository.findById(id).map(owner -> {
            owner.setFirstName(data.getFirstName());
            owner.setLastName(data.getLastName());
            owner.setAddress(data.getAddress());
            owner.setCity(data.getCity());
            owner.setTelephone(data.getTelephone());

            return repository.save(owner);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
