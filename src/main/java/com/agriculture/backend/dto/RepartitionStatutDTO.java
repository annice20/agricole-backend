package com.agriculture.backend.dto;

import com.agriculture.backend.model.StatutDemande;

public class RepartitionStatutDTO {
    private StatutDemande statut;
    private Long nombre;

    public RepartitionStatutDTO(StatutDemande statut, Long nombre) {
        this.statut = statut;
        this.nombre = nombre;
    }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
    public Long getNombre() { return nombre; }
    public void setNombre(Long nombre) { this.nombre = nombre; }
}