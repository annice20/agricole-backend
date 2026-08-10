package com.agriculture.backend.dto;

import com.agriculture.backend.model.TypeAide;
import java.math.BigDecimal;

public class RepartitionTypeAideDTO {
    private TypeAide typeAide;
    private Long nombre;
    private BigDecimal montantTotal;

    public RepartitionTypeAideDTO(TypeAide typeAide, Long nombre, BigDecimal montantTotal) {
        this.typeAide = typeAide;
        this.nombre = nombre;
        this.montantTotal = montantTotal;
    }

    public TypeAide getTypeAide() { return typeAide; }
    public void setTypeAide(TypeAide typeAide) { this.typeAide = typeAide; }
    public Long getNombre() { return nombre; }
    public void setNombre(Long nombre) { this.nombre = nombre; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
}