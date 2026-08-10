package com.agriculture.backend.dto;

import com.agriculture.backend.model.StatutDemande;

public class DemandeAideDTO {

    private String commentaire;
    private StatutDemande statut;
    private Long agriculteurId;
    private Long programmeId;

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public Long getAgriculteurId() { return agriculteurId; }
    public void setAgriculteurId(Long agriculteurId) { this.agriculteurId = agriculteurId; }

    public Long getProgrammeId() { return programmeId; }
    public void setProgrammeId(Long programmeId) { this.programmeId = programmeId; }
}