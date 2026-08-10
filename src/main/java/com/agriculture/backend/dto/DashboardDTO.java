package com.agriculture.backend.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private long agriculteurs;

    private long agriculteursTous;

    private long programmes;
    private long distributions;
    private double montantTotalFinancement;
    private List<Object> repartitionAides;
    private List<Map<String, Object>> analyseRegionale;

    public long getAgriculteurs() { return agriculteurs; }
    public void setAgriculteurs(long agriculteurs) { this.agriculteurs = agriculteurs; }

    public long getAgriculteursTous() { return agriculteursTous; }
    public void setAgriculteursTous(long agriculteursTous) { this.agriculteursTous = agriculteursTous; }

    public long getProgrammes() { return programmes; }
    public void setProgrammes(long programmes) { this.programmes = programmes; }

    public long getDistributions() { return distributions; }
    public void setDistributions(long distributions) { this.distributions = distributions; }

    public double getMontantTotalFinancement() { return montantTotalFinancement; }
    public void setMontantTotalFinancement(double montantTotalFinancement) {
        this.montantTotalFinancement = montantTotalFinancement;
    }

    public List<Object> getRepartitionAides() { return repartitionAides; }
    public void setRepartitionAides(List<Object> repartitionAides) {
        this.repartitionAides = repartitionAides;
    }

    public List<Map<String, Object>> getAnalyseRegionale() { return analyseRegionale; }
    public void setAnalyseRegionale(List<Map<String, Object>> analyseRegionale) {
        this.analyseRegionale = analyseRegionale;
    }
}