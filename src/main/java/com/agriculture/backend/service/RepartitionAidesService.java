package com.agriculture.backend.service;

import com.agriculture.backend.dto.RepartitionAidesDTO;
import com.agriculture.backend.dto.RepartitionStatutDTO;
import com.agriculture.backend.dto.RepartitionTypeAideDTO;
import com.agriculture.backend.repository.DemandeAideRepository;
import com.agriculture.backend.repository.DistributionAideRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class RepartitionAidesService {

    private final DistributionAideRepository distributionAideRepository;
    private final DemandeAideRepository demandeAideRepository;

    public RepartitionAidesService(DistributionAideRepository distributionAideRepository,
                                    DemandeAideRepository demandeAideRepository) {
        this.distributionAideRepository = distributionAideRepository;
        this.demandeAideRepository = demandeAideRepository;
    }

    public RepartitionAidesDTO getRepartition() {
        List<RepartitionTypeAideDTO> parType = distributionAideRepository.getRepartitionParType();
        List<RepartitionStatutDTO> parStatut = demandeAideRepository.getRepartitionParStatut();

        long totalDistributions = parType.stream()
                .mapToLong(RepartitionTypeAideDTO::getNombre)
                .sum();

        BigDecimal montantTotalDistribue = parType.stream()
                .map(RepartitionTypeAideDTO::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RepartitionAidesDTO dto = new RepartitionAidesDTO();
        dto.setParType(parType);
        dto.setParStatut(parStatut);
        dto.setTotalDistributions(totalDistributions);
        dto.setMontantTotalDistribue(montantTotalDistribue);
        return dto;
    }
}