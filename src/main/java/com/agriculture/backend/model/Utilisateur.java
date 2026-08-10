package com.agriculture.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateurs")
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;
    
    @Column(unique = true)
    private String telephone;

    private boolean actif;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_compte")
    private StatutCompte statutCompte = StatutCompte.EN_ATTENTE;

    private boolean doubleAuthentification;

    private String codeOtp;

    private LocalDateTime expirationOtp;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    private String resetToken;
    
    private LocalDateTime resetTokenExpiration;

	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region regionGeree;

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

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
	
	public StatutCompte getStatutCompte() {
	    return statutCompte;
	}
	
	public void setStatutCompte(StatutCompte statutCompte) {
	    this.statutCompte = statutCompte;
	}

	public boolean isDoubleAuthentification() {
		return doubleAuthentification;
	}

	public void setDoubleAuthentification(boolean doubleAuthentification) {
		this.doubleAuthentification = doubleAuthentification;
	}

	public String getCodeOtp() {
		return codeOtp;
	}

	public void setCodeOtp(String codeOtp) {
		this.codeOtp = codeOtp;
	}

	public LocalDateTime getExpirationOtp() {
		return expirationOtp;
	}

	public void setExpirationOtp(LocalDateTime expirationOtp) {
		this.expirationOtp = expirationOtp;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getResetTokenExpiration() {
		return resetTokenExpiration;
	}

	public void setResetTokenExpiration(LocalDateTime resetTokenExpiration) {
		this.resetTokenExpiration = resetTokenExpiration;
	}

	public Region getRegionGeree() {
		return regionGeree;
	}

	public void setRegionGeree(Region regionGeree) {
		this.regionGeree = regionGeree;
	}
}
