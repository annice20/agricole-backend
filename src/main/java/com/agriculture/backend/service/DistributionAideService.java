package com.agriculture.backend.service;

import com.agriculture.backend.dto.DistributionAideDTO;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.model.DemandeAide;
import com.agriculture.backend.model.DistributionAide;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.repository.DemandeAideRepository;
import com.agriculture.backend.repository.DistributionAideRepository;
import com.agriculture.backend.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributionAideService {

    private final DistributionAideRepository distributionRepository;
    private final DemandeAideRepository demandeAideRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AgriculteurRepository agriculteurRepository;
    private final NotificationService notificationService;

    @Autowired
    public DistributionAideService(
            DistributionAideRepository distributionRepository,
            DemandeAideRepository demandeAideRepository,
            UtilisateurRepository utilisateurRepository,
            AgriculteurRepository agriculteurRepository,
            NotificationService notificationService
    ) {
        this.distributionRepository = distributionRepository;
        this.demandeAideRepository = demandeAideRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.agriculteurRepository = agriculteurRepository;
        this.notificationService = notificationService;
    }

    public List<DistributionAide> getAll() {
        return distributionRepository.findAll();
    }

    public DistributionAide getById(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Distribution introuvable"));
    }

    public DistributionAide update(DistributionAide d) {
        return distributionRepository.save(d);
    }

    public void deleteById(Long id) {
        distributionRepository.deleteById(id);
    }

    public List<DistributionAide> getByAgriculteur(Long agriculteurId) {
        return distributionRepository.findByDemandeAideAgriculteurId(agriculteurId);
    }

    /**
     * Création d'une distribution d'aide
     *
     * Fonctionnalités :
     * - Enregistrement de la distribution
     * - Activation automatique du bénéficiaire
     * - Création d'une notification pour l'agriculteur
     */
    public DistributionAide createFromDTO(DistributionAideDTO dto) {
        DemandeAide demande =
                demandeAideRepository.findById(dto.getDemandeId())
                        .orElseThrow(() ->
                                new RuntimeException("Demande introuvable"));

        Utilisateur agent =
                utilisateurRepository.findById(dto.getAgentId())
                        .orElseThrow(() ->
                                new RuntimeException("Agent introuvable"));

        DistributionAide distribution = new DistributionAide();

        distribution.setDemandeAide(demande);
        distribution.setAgent(agent);
        distribution.setMontant(dto.getMontant());
        distribution.setDateDistribution(dto.getDateDistribution());
        distribution.setDescription(dto.getDescription());
        distribution.setPreuveDistribution(dto.getPreuveDistribution());

        DistributionAide saved = distributionRepository.save(distribution);

        /*
         * Gestion du bénéficiaire
         */
        Agriculteur agriculteur = demande.getAgriculteur();

        if (agriculteur != null) {
            // Activation automatique après attribution d'une aide
            if (!agriculteur.isActif()) {
                agriculteur.setActif(true);
                agriculteurRepository.save(agriculteur);
            }

            /*
             * Alerte de distribution
             */
            if (agriculteur.getUtilisateur() != null) {
                notificationService.creerNotification(
                        agriculteur.getUtilisateur().getId(),
                        "Nouvelle distribution d'aide",
                        "Votre aide agricole a été distribuée avec succès."
                );
            }
        }

        return saved;
    }
    
    public List<DistributionAide> getByRegionId(Long regionId, String emailUtilisateurConnecte) {
        Utilisateur user = utilisateurRepository.findByEmail(emailUtilisateurConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + emailUtilisateurConnecte));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getNom() == com.agriculture.backend.model.RoleName.ADMIN_NATIONAL);

        if (!isAdmin && (user.getRegionGeree() == null
                || !user.getRegionGeree().getId().equals(regionId))) {
            throw new RuntimeException("Accès refusé : cette région ne correspond pas à votre compte");
        }

        return distributionRepository.findByRegionId(regionId);
    }
}