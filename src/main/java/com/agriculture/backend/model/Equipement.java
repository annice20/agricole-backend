package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipements")
@NoArgsConstructor
@AllArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String categorie;

    private Integer quantiteDisponible;

    private String description;

    private Boolean actif = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public Integer getQuantiteDisponible() {
		return quantiteDisponible;
	}

	public void setQuantiteDisponible(Integer quantiteDisponible) {
		this.quantiteDisponible = quantiteDisponible;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActif() {
		return actif;
	}

	public void setActif(Boolean actif) {
		this.actif = actif;
	}
    
}