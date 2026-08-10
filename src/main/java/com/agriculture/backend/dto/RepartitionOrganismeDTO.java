package com.agriculture.backend.dto;

import java.math.BigDecimal;

public class RepartitionOrganismeDTO {
    private String organisme;
    private Long nombre;
    private BigDecimal montantTotal;

    public RepartitionOrganismeDTO(String organisme, Long nombre, BigDecimal montantTotal) {
        this.organisme = organisme;
        this.nombre = nombre;
        this.montantTotal = montantTotal;
    }

    public String getOrganisme() { return organisme; }
    public void setOrganisme(String organisme) { this.organisme = organisme; }
    public Long getNombre() { return nombre; }
    public void setNombre(Long nombre) { this.nombre = nombre; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
}