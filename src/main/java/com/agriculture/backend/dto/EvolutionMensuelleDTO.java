package com.agriculture.backend.dto;

import java.math.BigDecimal;

public class EvolutionMensuelleDTO {
    private String periode; // format "yyyy-MM"
    private BigDecimal montantTotal;

    public EvolutionMensuelleDTO(String periode, BigDecimal montantTotal) {
        this.periode = periode;
        this.montantTotal = montantTotal;
    }

    public String getPeriode() { return periode; }
    public void setPeriode(String periode) { this.periode = periode; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
}