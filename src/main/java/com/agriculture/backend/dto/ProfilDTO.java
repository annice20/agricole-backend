package com.agriculture.backend.dto;

import java.util.List;

public class ProfilDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private boolean doubleAuthentification;
    private List<String> roles;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isDoubleAuthentification() { return doubleAuthentification; }
    public void setDoubleAuthentification(boolean doubleAuthentification) { this.doubleAuthentification = doubleAuthentification; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}