package com.agriculture.backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agriculture.backend.dto.FinancementAgricoleDTO;
import com.agriculture.backend.model.FinancementAgricole;
import com.agriculture.backend.model.ProgrammeAide;
import com.agriculture.backend.repository.FinancementAgricoleRepository;
import com.agriculture.backend.repository.ProgrammeAideRepository;
import com.agriculture.backend.service.FinancementAgricoleService;

@Service
public class FinancementAgricoleServiceImpl
        implements FinancementAgricoleService {

    private final FinancementAgricoleRepository financementRepository;
    private final ProgrammeAideRepository programmeRepository;

    public FinancementAgricoleServiceImpl(
            FinancementAgricoleRepository financementRepository,
            ProgrammeAideRepository programmeRepository) {

        this.financementRepository = financementRepository;
        this.programmeRepository = programmeRepository;
    }

    @Override
    public FinancementAgricoleDTO creer(
            FinancementAgricoleDTO dto) {

        ProgrammeAide programme =
                programmeRepository.findById(dto.getProgrammeId())
                .orElseThrow(() ->
                new RuntimeException("Programme introuvable"));

        FinancementAgricole financement =
                new FinancementAgricole();

        financement.setOrganisme(dto.getOrganisme());
        financement.setMontant(dto.getMontant());
        financement.setDateFinancement(dto.getDateFinancement());
        financement.setDescription(dto.getDescription());
        financement.setProgramme(programme);

        return convertirDTO(
                financementRepository.save(financement));
    }

    @Override
    public FinancementAgricoleDTO modifier(
            Long id,
            FinancementAgricoleDTO dto) {

        FinancementAgricole financement =
                financementRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("Financement introuvable"));

        ProgrammeAide programme =
                programmeRepository.findById(dto.getProgrammeId())
                .orElseThrow(() ->
                new RuntimeException("Programme introuvable"));

        financement.setOrganisme(dto.getOrganisme());
        financement.setMontant(dto.getMontant());
        financement.setDateFinancement(dto.getDateFinancement());
        financement.setDescription(dto.getDescription());
        financement.setProgramme(programme);

        return convertirDTO(
                financementRepository.save(financement));
    }

    @Override
    public void supprimer(Long id) {
        financementRepository.deleteById(id);
    }

    @Override
    public FinancementAgricoleDTO obtenirParId(Long id) {

        return convertirDTO(
                financementRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("Financement introuvable")));
    }

    @Override
    public List<FinancementAgricoleDTO> obtenirTous() {

        return financementRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    private FinancementAgricoleDTO convertirDTO(
            FinancementAgricole financement) {

        FinancementAgricoleDTO dto =
                new FinancementAgricoleDTO();

        dto.setId(financement.getId());
        dto.setOrganisme(financement.getOrganisme());
        dto.setMontant(financement.getMontant());
        dto.setDateFinancement(financement.getDateFinancement());
        dto.setDescription(financement.getDescription());

        if (financement.getProgramme() != null) {
            dto.setProgrammeId(financement.getProgramme().getId());

            dto.setProgrammeNom(financement.getProgramme().getTitre());
        }

        return dto;
    }
}