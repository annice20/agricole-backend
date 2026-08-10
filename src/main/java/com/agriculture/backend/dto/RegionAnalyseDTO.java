package com.agriculture.backend.dto;

public class RegionAnalyseDTO {
    private String nomRegion;
    private long totalAgriculteurs;
    private long totalAgriculteursActifs;
    private long nombreDistributions;
    private double montantDistribue;
    private double montantMoyenParActif;

    public String getNomRegion() { return nomRegion; }
    public void setNomRegion(String nomRegion) { this.nomRegion = nomRegion; }
    public long getTotalAgriculteurs() { return totalAgriculteurs; }
    public void setTotalAgriculteurs(long totalAgriculteurs) { this.totalAgriculteurs = totalAgriculteurs; }
    public long getTotalAgriculteursActifs() { return totalAgriculteursActifs; }
    public void setTotalAgriculteursActifs(long totalAgriculteursActifs) { this.totalAgriculteursActifs = totalAgriculteursActifs; }
    public long getNombreDistributions() { return nombreDistributions; }
    public void setNombreDistributions(long nombreDistributions) { this.nombreDistributions = nombreDistributions; }
    public double getMontantDistribue() { return montantDistribue; }
    public void setMontantDistribue(double montantDistribue) { this.montantDistribue = montantDistribue; }
    public double getMontantMoyenParActif() { return montantMoyenParActif; }
    public void setMontantMoyenParActif(double montantMoyenParActif) { this.montantMoyenParActif = montantMoyenParActif; }
    public static RegionAnalyseDTO nouveauVide(String nomRegion) {
        RegionAnalyseDTO dto = new RegionAnalyseDTO();
        dto.setNomRegion(nomRegion);
        return dto;
    }
}