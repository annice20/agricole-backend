package com.agriculture.backend.service;

import com.agriculture.backend.model.Equipement;
import com.agriculture.backend.repository.EquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipementService {

    private final EquipementRepository equipementRepository;

    @Autowired
    public EquipementService(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    public List<Equipement> getAll() {
        return equipementRepository.findAll();
    }

    public Equipement getById(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipement introuvable"));
    }

    public Equipement create(Equipement equipement) {

        if (equipement.getActif() == null) {
            equipement.setActif(true);
        }

        return equipementRepository.save(equipement);
    }

    public Equipement update(Long id, Equipement equipement) {

        Equipement existing =
                equipementRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Equipement introuvable"));
        existing.setNom(equipement.getNom());
        existing.setCategorie(equipement.getCategorie());
        existing.setDescription(equipement.getDescription());
        existing.setQuantiteDisponible(equipement.getQuantiteDisponible());
        existing.setActif(equipement.getActif());

        return equipementRepository.save(existing);
    }

    public void delete(Long id) {
        equipementRepository.deleteById(id);
    }
}