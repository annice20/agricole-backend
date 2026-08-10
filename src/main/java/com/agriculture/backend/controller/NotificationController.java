package com.agriculture.backend.controller;

import com.agriculture.backend.model.Notification;
import com.agriculture.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationService notificationService;
    
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/utilisateur/{id}")
    public List<Notification> getNotifications(@PathVariable Long id) {
        return notificationService.getNotifications(id);
    }

    @PutMapping("/{id}/lu")
    public void marquerCommeLu(@PathVariable Long id) {
        notificationService.marquerCommeLu(id);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_NATIONAL', 'RESPONSABLE_REGIONAL', 'AGENT_TERRAIN')")
    public Notification creerNotification(@RequestBody Map<String, Object> body) {
        Long utilisateurId = Long.valueOf(String.valueOf(body.get("utilisateurId")));
        String titre = (String) body.get("titre");
        String message = (String) body.get("message");
        return notificationService.creerNotification(utilisateurId, titre, message);
    }
}