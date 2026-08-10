package com.agriculture.backend.dto;

public class ReclamationDTO {

    private String sujet;
    private String description;
    private Long agriculteurId;

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getAgriculteurId() { return agriculteurId; }
    public void setAgriculteurId(Long agriculteurId) { this.agriculteurId = agriculteurId; }
}