package com.agriculture.backend.dto;

public class ActivationRequest {

    private String statusCompte; // "ACTIF" ou "EN_ATTENTE"

    public ActivationRequest() {
    }

    public ActivationRequest(String statusCompte) {
        this.statusCompte = statusCompte;
    }

    public String getStatusCompte() {
        return statusCompte;
    }

    public void setStatusCompte(String statusCompte) {
        this.statusCompte = statusCompte;
    }
}