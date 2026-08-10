package com.agriculture.backend.service;

import com.agriculture.backend.dto.DemandeAideDTO;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.model.DemandeAide;
import com.agriculture.backend.model.Notification;
import com.agriculture.backend.model.ProgrammeAide;
import com.agriculture.backend.model.StatutDemande;
import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.repository.DemandeAideRepository;
import com.agriculture.backend.repository.NotificationRepository;
import com.agriculture.backend.repository.ProgrammeAideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandeAideService {

    private final DemandeAideRepository demandeRepository;
    private final AgriculteurRepository agriculteurRepository;
    private final ProgrammeAideRepository programmeAideRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public DemandeAideService(
            DemandeAideRepository demandeRepository,
            AgriculteurRepository agriculteurRepository,
            ProgrammeAideRepository programmeAideRepository,
            NotificationRepository notificationRepository
    ) {
        this.demandeRepository = demandeRepository;
        this.agriculteurRepository = agriculteurRepository;
        this.programmeAideRepository = programmeAideRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<DemandeAide> getAll() {
        return demandeRepository.findAll();
    }

    public DemandeAide getById(Long id) {
        return demandeRepository.findById(id).orElseThrow();
    }

    public DemandeAide createFromDTO(DemandeAideDTO dto) {

        Agriculteur agriculteur = agriculteurRepository
                .findById(dto.getAgriculteurId())
                .orElseThrow(() -> new RuntimeException("Agriculteur introuvable"));

        ProgrammeAide programme = programmeAideRepository
                .findById(dto.getProgrammeId())
                .orElseThrow(() -> new RuntimeException("Programme introuvable"));

        DemandeAide demande = new DemandeAide();
        demande.setCommentaire(dto.getCommentaire());
        demande.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutDemande.EN_ATTENTE);
        demande.setAgriculteur(agriculteur);
        demande.setProgramme(programme);
        demande.setDateDemande(LocalDateTime.now()); // date automatique

        return demandeRepository.save(demande);
    }

    public DemandeAide update(DemandeAide demande) {
        return demandeRepository.save(demande);
    }

    public void deleteById(Long id) {
        demandeRepository.deleteById(id);
    }
    
    @Transactional
    public DemandeAide changerStatut(Long demandeId, StatutDemande statut) {
        DemandeAide demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        StatutDemande ancienStatut = demande.getStatut();
        demande.setStatut(statut);

        // On notifie l'agriculteur à chaque changement de statut réel
        if (ancienStatut != statut) {
            String titre;
            String message;

            switch (statut) {
                case VALIDEE:
                    titre = "Demande validée";
                    message = "Bonjour " + demande.getAgriculteur().getNom()
                            + ", votre demande d'aide a été validée.";
                    break;
                case REFUSEE:
                    titre = "Demande refusée";
                    message = "Bonjour " + demande.getAgriculteur().getNom()
                            + ", votre demande d'aide a été refusée.";
                    break;
                case DISPONIBLE:
                    titre = "Aide agricole disponible";
                    message = "Bonjour " + demande.getAgriculteur().getNom()
                            + ", votre aide est maintenant disponible.";
                    break;
                case DISTRIBUEE:
                    titre = "Aide distribuée";
                    message = "Bonjour " + demande.getAgriculteur().getNom()
                            + ", votre aide vous a été distribuée.";
                    break;
                default:
                    titre = null;
                    message = null;
            }

            if (titre != null) {
                Notification notif = new Notification();
                notif.setTitre(titre);
                notif.setMessage(message);
                notif.setDateEnvoi(LocalDateTime.now());
                notif.setLu(false);
                notif.setUtilisateur(demande.getAgriculteur().getUtilisateur());
                notificationRepository.save(notif);
            }
        }

        return demande;
    }
}