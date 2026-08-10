package com.agriculture.backend.service;

import java.util.List;

import com.agriculture.backend.dto.FinancementAgricoleDTO;

public interface FinancementAgricoleService {

    FinancementAgricoleDTO creer(FinancementAgricoleDTO dto);

    FinancementAgricoleDTO modifier(Long id, FinancementAgricoleDTO dto);

    void supprimer(Long id);

    FinancementAgricoleDTO obtenirParId(Long id);

    List<FinancementAgricoleDTO> obtenirTous();
}