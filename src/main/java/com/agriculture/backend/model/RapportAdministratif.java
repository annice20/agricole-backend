package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapports_administratifs")
@NoArgsConstructor
@AllArgsConstructor
public class RapportAdministratif {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String fichier;

    private LocalDateTime dateGeneration;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur generePar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getFichier() {
		return fichier;
	}

	public void setFichier(String fichier) {
		this.fichier = fichier;
	}

	public LocalDateTime getDateGeneration() {
		return dateGeneration;
	}

	public void setDateGeneration(LocalDateTime dateGeneration) {
		this.dateGeneration = dateGeneration;
	}

	public Utilisateur getGenerePar() {
		return generePar;
	}

	public void setGenerePar(Utilisateur generePar) {
		this.generePar = generePar;
	}
    
}
