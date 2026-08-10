package com.agriculture.backend.dto;

import java.math.BigDecimal;

public class SuiviProgrammeDTO {
    private Long programmeId;
    private String titreProgramme;
    private BigDecimal budget;
    private BigDecimal montantFinance;
    private Double tauxCouverture; // calculé en service, pas en JPQL

    // Constructeur utilisé par la requête JPQL (sans tauxCouverture)
    public SuiviProgrammeDTO(Long programmeId, String titreProgramme, BigDecimal budget, BigDecimal montantFinance) {
        this.programmeId = programmeId;
        this.titreProgramme = titreProgramme;
        this.budget = budget;
        this.montantFinance = montantFinance;
    }

    public Long getProgrammeId() { return programmeId; }
    public void setProgrammeId(Long programmeId) { this.programmeId = programmeId; }
    public String getTitreProgramme() { return titreProgramme; }
    public void setTitreProgramme(String titreProgramme) { this.titreProgramme = titreProgramme; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public BigDecimal getMontantFinance() { return montantFinance; }
    public void setMontantFinance(BigDecimal montantFinance) { this.montantFinance = montantFinance; }
    public Double getTauxCouverture() { return tauxCouverture; }
    public void setTauxCouverture(Double tauxCouverture) { this.tauxCouverture = tauxCouverture; }
}