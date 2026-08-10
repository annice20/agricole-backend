package com.agriculture.backend.service;

import com.agriculture.backend.dto.*;
import com.agriculture.backend.model.FinancementAgricole;
import com.agriculture.backend.repository.FinancementAgricoleRepository;
import com.agriculture.backend.repository.ProgrammeAideRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class FinancementSuiviService {

    private final FinancementAgricoleRepository financementAgricoleRepository;
    private final ProgrammeAideRepository programmeAideRepository;

    public FinancementSuiviService(FinancementAgricoleRepository financementAgricoleRepository,
                                    ProgrammeAideRepository programmeAideRepository) {
        this.financementAgricoleRepository = financementAgricoleRepository;
        this.programmeAideRepository = programmeAideRepository;
    }

    public SuiviFinancementsDTO getSuivi() {
        List<RepartitionOrganismeDTO> parOrganisme = financementAgricoleRepository.getRepartitionParOrganisme();

        List<SuiviProgrammeDTO> parProgramme = programmeAideRepository.getSuiviParProgramme();
        for (SuiviProgrammeDTO p : parProgramme) {
            if (p.getBudget() != null && p.getBudget().compareTo(BigDecimal.ZERO) > 0) {
                double taux = p.getMontantFinance()
                        .divide(p.getBudget(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                p.setTauxCouverture(taux);
            } else {
                p.setTauxCouverture(0.0);
            }
        }

        List<FinancementAgricole> tousFinancements = financementAgricoleRepository.findAllByOrderByDateFinancementAsc();
        Map<YearMonth, BigDecimal> parMois = new TreeMap<>();
        for (FinancementAgricole f : tousFinancements) {
            if (f.getDateFinancement() == null) continue;
            YearMonth ym = YearMonth.from(f.getDateFinancement());
            BigDecimal montant = f.getMontant() != null ? f.getMontant() : BigDecimal.ZERO;
            parMois.merge(ym, montant, BigDecimal::add);
        }
        List<EvolutionMensuelleDTO> evolutionMensuelle = parMois.entrySet().stream()
                .map(e -> new EvolutionMensuelleDTO(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());

        BigDecimal montantTotalGlobal = parOrganisme.stream()
                .map(RepartitionOrganismeDTO::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SuiviFinancementsDTO dto = new SuiviFinancementsDTO();
        dto.setParOrganisme(parOrganisme);
        dto.setParProgramme(parProgramme);
        dto.setEvolutionMensuelle(evolutionMensuelle);
        dto.setMontantTotalGlobal(montantTotalGlobal);
        dto.setNombreOrganismes(parOrganisme.size());
        return dto;
    }
}