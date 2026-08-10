package com.agriculture.backend.repository;

import com.agriculture.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurIdOrderByDateEnvoiDesc(Long utilisateurId);
}