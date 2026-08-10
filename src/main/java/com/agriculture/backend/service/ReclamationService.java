package com.agriculture.backend.service;

import com.agriculture.backend.dto.ReclamationDTO;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.model.Notification;
import com.agriculture.backend.model.Reclamation;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.repository.NotificationRepository;
import com.agriculture.backend.repository.ReclamationRepository;
import com.agriculture.backend.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final AgriculteurRepository agriculteurRepository;
    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public ReclamationService(
            ReclamationRepository reclamationRepository,
            AgriculteurRepository agriculteurRepository,
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.reclamationRepository = reclamationRepository;
        this.agriculteurRepository = agriculteurRepository;
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Reclamation> getAll() {
        return reclamationRepository.findAll();
    }

    public List<Reclamation> getByAgriculteurId(Long agriculteurId) {
        return reclamationRepository.findByAgriculteurId(agriculteurId);
    }
    
    public List<Reclamation> getByRegionId(Long regionId, String emailUtilisateurConnecte) {
        Utilisateur user = utilisateurRepository.findByEmail(emailUtilisateurConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + emailUtilisateurConnecte));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);

        if (!isAdmin && (user.getRegionGeree() == null
                || !user.getRegionGeree().getId().equals(regionId))) {
            throw new RuntimeException("Accès refusé : cette région ne correspond pas à votre compte");
        }

        return reclamationRepository.findByRegionId(regionId);
    }

    @Transactional
    public Reclamation createFromDTO(ReclamationDTO dto) {
        Agriculteur agriculteur = agriculteurRepository
                .findById(dto.getAgriculteurId())
                .orElseThrow(() -> new RuntimeException("Agriculteur introuvable"));

        Reclamation reclamation = new Reclamation();
        reclamation.setSujet(dto.getSujet());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut("EN_ATTENTE");
        reclamation.setDateCreation(LocalDateTime.now());
        reclamation.setAgriculteur(agriculteur);
        Reclamation sauvegardee = reclamationRepository.save(reclamation);

        // Notification des rôles concernés : admin national (toujours) +
        // responsable régional de la région de l'agriculteur (si assigné)
        notifierNouvelleReclamation(sauvegardee, agriculteur);

        return sauvegardee;
    }

    private void notifierNouvelleReclamation(Reclamation reclamation, Agriculteur agriculteur) {
        String titre = "Nouvelle réclamation reçue";
        String message = "Une nouvelle réclamation \"" + reclamation.getSujet()
                + "\" a été déposée par " + agriculteur.getPrenom() + " " + agriculteur.getNom() + ".";

        try {
            // Tous les administrateurs nationaux
            List<Utilisateur> admins = utilisateurRepository.findByRoles_Nom(RoleName.ADMIN_NATIONAL);
            for (Utilisateur admin : admins) {
                Notification notif = new Notification();
                notif.setTitre(titre);
                notif.setMessage(message);
                notif.setDateEnvoi(LocalDateTime.now());
                notif.setLu(false);
                notif.setUtilisateur(admin);
                notificationRepository.save(notif);
            }

            // Le responsable régional de la région de l'agriculteur, s'il existe
            if (agriculteur.getDistrict() != null && agriculteur.getDistrict().getRegion() != null) {
                Long regionId = agriculteur.getDistrict().getRegion().getId();
                List<Utilisateur> responsables = utilisateurRepository
                        .findByRoles_NomAndRegionGeree_Id(RoleName.RESPONSABLE_REGIONAL, regionId);
                for (Utilisateur responsable : responsables) {
                    Notification notif = new Notification();
                    notif.setTitre(titre);
                    notif.setMessage(message);
                    notif.setDateEnvoi(LocalDateTime.now());
                    notif.setLu(false);
                    notif.setUtilisateur(responsable);
                    notificationRepository.save(notif);
                }
            }
        } catch (Exception e) {
            // La notification ne doit jamais faire échouer la création de la réclamation
            System.err.println("Erreur lors de la notification de la réclamation : " + e.getMessage());
        }
    }

    public void deleteById(Long id) {
        reclamationRepository.deleteById(id);
    }

    /*
        ✅ AJOUT : notification automatique à l'agriculteur quand sa
        réclamation passe à un statut terminal (TRAITEE ou REJETEE),
        même pattern que DemandeAideService.changerStatut().
        Sans ça, l'agriculteur n'a aucun moyen de savoir que sa
        réclamation a été traitée sans revenir manuellement sur la page.
    */
    @Transactional
    public Reclamation changerStatut(Long id, String statut) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation introuvable"));

        String ancienStatut = reclamation.getStatut();
        reclamation.setStatut(statut);

        if (!statut.equals(ancienStatut)) {
            String titre = null;
            String message = null;

            if ("TRAITEE".equals(statut)) {
                titre = "Réclamation traitée";
                message = "Bonjour " + reclamation.getAgriculteur().getNom()
                        + ", votre réclamation \"" + reclamation.getSujet()
                        + "\" a été traitée par nos équipes.";
            } else if ("REJETEE".equals(statut)) {
                titre = "Réclamation rejetée";
                message = "Bonjour " + reclamation.getAgriculteur().getNom()
                        + ", votre réclamation \"" + reclamation.getSujet()
                        + "\" a été examinée et rejetée.";
            }

            if (titre != null) {
                Notification notif = new Notification();
                notif.setTitre(titre);
                notif.setMessage(message);
                notif.setDateEnvoi(LocalDateTime.now());
                notif.setLu(false);
                notif.setUtilisateur(reclamation.getAgriculteur().getUtilisateur());
                notificationRepository.save(notif);
            }
        }

        return reclamationRepository.save(reclamation);
    }
}