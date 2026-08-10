package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "distributions_aides")
@NoArgsConstructor
@AllArgsConstructor
public class DistributionAide {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDistribution;

    private BigDecimal montant;

    private String description;

    private String preuveDistribution;
    
    @ManyToOne
    @JoinColumn(name = "demande_id")
    private DemandeAide demandeAide;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Utilisateur agent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDateDistribution() {
		return dateDistribution;
	}

	public void setDateDistribution(LocalDate dateDistribution) {
		this.dateDistribution = dateDistribution;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPreuveDistribution() {
		return preuveDistribution;
	}

	public void setPreuveDistribution(String preuveDistribution) {
		this.preuveDistribution = preuveDistribution;
	}

	public DemandeAide getDemandeAide() {
		return demandeAide;
	}

	public void setDemandeAide(DemandeAide demandeAide) {
		this.demandeAide = demandeAide;
	}

	public Utilisateur getAgent() {
		return agent;
	}

	public void setAgent(Utilisateur agent) {
		this.agent = agent;
	}
    
}
