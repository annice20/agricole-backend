package com.agriculture.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_actions")
public class HistoriqueAction {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String utilisateur;

    private String role;

    @Column(nullable = false)
    private String action;

    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateAction;

    public HistoriqueAction() {

    }

    public HistoriqueAction(
            String utilisateur,
            String role,
            String action,
            String module,
            String description
    ) {
        this.utilisateur = utilisateur;
        this.role = role;
        this.action = action;
        this.module = module;
        this.description = description;
        this.dateAction = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {

        if(dateAction == null){
            dateAction = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
