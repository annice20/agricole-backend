package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_aides")
@NoArgsConstructor
@AllArgsConstructor
public class DemandeAide {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDemande;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    @Column(columnDefinition = "TEXT")
    private String commentaire;
    
    @ManyToOne
    @JoinColumn(name = "agriculteur_id")
    private Agriculteur agriculteur;

    @ManyToOne
    @JoinColumn(name = "programme_id")
    private ProgrammeAide programme;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(LocalDateTime dateDemande) {
		this.dateDemande = dateDemande;
	}

	public StatutDemande getStatut() {
		return statut;
	}

	public void setStatut(StatutDemande statut) {
		this.statut = statut;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Agriculteur getAgriculteur() {
		return agriculteur;
	}

	public void setAgriculteur(Agriculteur agriculteur) {
		this.agriculteur = agriculteur;
	}

	public ProgrammeAide getProgramme() {
		return programme;
	}

	public void setProgramme(ProgrammeAide programme) {
		this.programme = programme;
	}
    
}
