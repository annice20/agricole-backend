package com.agriculture.backend.dto;

public class AuthResponse {

    private String message;
    private String token;
    private boolean otpRequis;
    private String role;
    private Long id;
    private String nom;
    private String prenom;
    private String statusCompte;
    private Long regionId;
    private String regionNom;

    // Constructeur simple (OTP envoyé, déconnexion, inscription)
    public AuthResponse(String message, String token, boolean otpRequis, String role) {
        this.message = message;
        this.token = token;
        this.otpRequis = otpRequis;
        this.role = role;
    }

    // Constructeur avec statusCompte (compte en attente d'activation)
    public AuthResponse(String message, String token, boolean otpRequis, String role, String statusCompte) {
        this(message, token, otpRequis, role);
        this.statusCompte = statusCompte;
    }

    // Constructeur avec id/nom/prenom (connexion réussie)
    public AuthResponse(String message, String token, boolean otpRequis, String role, Long id, String nom, String prenom) {
        this(message, token, otpRequis, role);
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    // constructeur avec région (pour RESPONSABLE_REGIONAL)
    public AuthResponse(String message, String token, boolean otpRequis, String role,
                         Long id, String nom, String prenom, Long regionId, String regionNom) {
        this(message, token, otpRequis, role, id, nom, prenom);
        this.regionId = regionId;
        this.regionNom = regionNom;
    }

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public boolean isOtpRequis() { return otpRequis; }
    public String getRole() { return role; }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getStatusCompte() { return statusCompte; }
    public Long getRegionId() { return regionId; }
    public String getRegionNom() { return regionNom; }
}