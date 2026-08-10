package com.agriculture.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancementAgricoleDTO {

    private Long id;

    private String organisme;

    private BigDecimal montant;

    private LocalDate dateFinancement;

    private String description;

    private Long programmeId;

    private String programmeNom;

    public FinancementAgricoleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganisme() {
        return organisme;
    }

    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateFinancement() {
        return dateFinancement;
    }

    public void setDateFinancement(LocalDate dateFinancement) {
        this.dateFinancement = dateFinancement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProgrammeId() {
        return programmeId;
    }

    public void setProgrammeId(Long programmeId) {
        this.programmeId = programmeId;
    }

    public String getProgrammeNom() {
        return programmeNom;
    }

    public void setProgrammeNom(String programmeNom) {
        this.programmeNom = programmeNom;
    }
}