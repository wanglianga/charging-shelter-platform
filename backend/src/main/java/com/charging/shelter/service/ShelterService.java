package com.charging.shelter.service;

import com.charging.shelter.entity.Shelter;
import com.charging.shelter.repository.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelterService {

    @Autowired
    private ShelterRepository shelterRepository;

    public List<Shelter> findAll() {
        return shelterRepository.findAll();
    }

    public Optional<Shelter> findById(Long id) {
        return shelterRepository.findById(id);
    }

    public Shelter save(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public void deleteById(Long id) {
        shelterRepository.deleteById(id);
    }

    public Shelter updateAvailableSockets(Long shelterId, Integer availableSockets) {
        Optional<Shelter> shelterOpt = shelterRepository.findById(shelterId);
        if (shelterOpt.isPresent()) {
            Shelter shelter = shelterOpt.get();
            shelter.setAvailableSockets(availableSockets);
            return shelterRepository.save(shelter);
        }
        return null;
    }
}
