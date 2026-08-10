package com.agriculture.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class RepartitionAidesDTO {
    private List<RepartitionTypeAideDTO> parType;
    private List<RepartitionStatutDTO> parStatut;
    private long totalDistributions;
    private BigDecimal montantTotalDistribue;

    public List<RepartitionTypeAideDTO> getParType() { return parType; }
    public void setParType(List<RepartitionTypeAideDTO> parType) { this.parType = parType; }
    public List<RepartitionStatutDTO> getParStatut() { return parStatut; }
    public void setParStatut(List<RepartitionStatutDTO> parStatut) { this.parStatut = parStatut; }
    public long getTotalDistributions() { return totalDistributions; }
    public void setTotalDistributions(long totalDistributions) { this.totalDistributions = totalDistributions; }
    public BigDecimal getMontantTotalDistribue() { return montantTotalDistribue; }
    public void setMontantTotalDistribue(BigDecimal montantTotalDistribue) { this.montantTotalDistribue = montantTotalDistribue; }
}