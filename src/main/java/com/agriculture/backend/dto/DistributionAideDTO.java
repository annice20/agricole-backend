package com.agriculture.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DistributionAideDTO {
    private Long demandeId;
    private Long agentId;
    private BigDecimal montant;
    private LocalDate dateDistribution;
    private String description;
    private String preuveDistribution;

    public Long getDemandeId() { return demandeId; }
    public void setDemandeId(Long demandeId) { this.demandeId = demandeId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDate getDateDistribution() { return dateDistribution; }
    public void setDateDistribution(LocalDate d) { this.dateDistribution = d; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPreuveDistribution() { return preuveDistribution; }
    public void setPreuveDistribution(String p) { this.preuveDistribution = p; }
}