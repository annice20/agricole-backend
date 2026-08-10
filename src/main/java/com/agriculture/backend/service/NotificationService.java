package com.agriculture.backend.service;

import com.agriculture.backend.model.Notification;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.repository.NotificationRepository;
import com.agriculture.backend.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Notification> getNotifications(Long utilisateurId) {
        return notificationRepository
                .findByUtilisateurIdOrderByDateEnvoiDesc(utilisateurId);
    }

    public void marquerCommeLu(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        n.setLu(true);
        notificationRepository.save(n);
    }
    
    public Notification creerNotification(Long utilisateurId, String titre, String message) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setTitre(titre != null ? titre : "Notification");
        notification.setMessage(message);
        notification.setLu(false);
        notification.setDateEnvoi(LocalDateTime.now());

        return notificationRepository.save(notification);
    }
}