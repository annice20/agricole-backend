package com.agriculture.backend.dto;

public class GeoAgriculteurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String typeCulture;
    private Double superficie;
    private String adresse;
    private boolean actif;
    private Double latitude;
    private Double longitude;
    private String nomDistrict;
    private String nomRegion;

    public GeoAgriculteurDTO() {}

    public GeoAgriculteurDTO(Long id, String nom, String prenom, String typeCulture,
                              Double superficie, String adresse, boolean actif,
                              Double latitude, Double longitude,
                              String nomDistrict, String nomRegion) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.typeCulture = typeCulture;
        this.superficie = superficie;
        this.adresse = adresse;
        this.actif = actif;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nomDistrict = nomDistrict;
        this.nomRegion = nomRegion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTypeCulture() { return typeCulture; }
    public void setTypeCulture(String typeCulture) { this.typeCulture = typeCulture; }
    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getNomDistrict() { return nomDistrict; }
    public void setNomDistrict(String nomDistrict) { this.nomDistrict = nomDistrict; }
    public String getNomRegion() { return nomRegion; }
    public void setNomRegion(String nomRegion) { this.nomRegion = nomRegion; }
}