package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financements_agricoles")
@NoArgsConstructor
@AllArgsConstructor
public class FinancementAgricole {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organisme;

    private BigDecimal montant;

    private LocalDate dateFinancement;

    private String description;
    
    @ManyToOne
    @JoinColumn(name = "programme_id")
    private ProgrammeAide programme;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrganisme() {
		return organisme;
	}

	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public LocalDate getDateFinancement() {
		return dateFinancement;
	}

	public void setDateFinancement(LocalDate dateFinancement) {
		this.dateFinancement = dateFinancement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProgrammeAide getProgramme() {
		return programme;
	}

	public void setProgramme(ProgrammeAide programme) {
		this.programme = programme;
	}
    
}
