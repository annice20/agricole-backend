package com.agriculture.backend.dto;

import java.util.List;

public class AnalyseRegionaleDTO {
    private List<RegionAnalyseDTO> regions;
    private String regionPlusActifs;
    private String regionPlusDistributions;
    private double montantTotalDistribue;

    public List<RegionAnalyseDTO> getRegions() { return regions; }
    public void setRegions(List<RegionAnalyseDTO> regions) { this.regions = regions; }
    public String getRegionPlusActifs() { return regionPlusActifs; }
    public void setRegionPlusActifs(String regionPlusActifs) { this.regionPlusActifs = regionPlusActifs; }
    public String getRegionPlusDistributions() { return regionPlusDistributions; }
    public void setRegionPlusDistributions(String regionPlusDistributions) { this.regionPlusDistributions = regionPlusDistributions; }
    public double getMontantTotalDistribue() { return montantTotalDistribue; }
    public void setMontantTotalDistribue(double montantTotalDistribue) { this.montantTotalDistribue = montantTotalDistribue; }
}