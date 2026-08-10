package com.agriculture.backend.service;

import org.springframework.stereotype.Service;

import com.agriculture.backend.model.HistoriqueAction;
import com.agriculture.backend.repository.HistoriqueActionRepository;

import java.util.List;

@Service
public class HistoriqueActionService {
	
	private final HistoriqueActionRepository historiqueRepository;

    public HistoriqueActionService(HistoriqueActionRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    /**
     * Récupérer tout l'historique
     * du plus récent au plus ancien
     */
    public List<HistoriqueAction> getHistorique() {
        return historiqueRepository.findAllByOrderByDateActionDesc();
    }

    /**
     * Recherche par utilisateur
     */
    public List<HistoriqueAction> rechercherParUtilisateur(String utilisateur) {
        return historiqueRepository.findByUtilisateurContainingIgnoreCase(utilisateur);
    }

    /**
     * Recherche par module
     */
    public List<HistoriqueAction> rechercherParModule(String module) {
        return historiqueRepository.findByModule(module);
    }

    /**
     * Enregistrer une nouvelle action
     */
    public HistoriqueAction enregistrer(HistoriqueAction historique) {
        return historiqueRepository.save(historique);
    }

    /**
     * Création simplifiée d'une action
     *
     * Exemple :
     * enregistrerAction(
     * "Jean",
     * "ADMIN_NATIONAL",
     * "VALIDATION",
     * "Demande",
     * "Validation demande #12"
     * )
     */
    public HistoriqueAction enregistrerAction(String utilisateur, String role, String action, String module, String description) {
        HistoriqueAction historique = new HistoriqueAction(
                        utilisateur,
                        role,
                        action,
                        module,
                        description
                );

        return historiqueRepository.save(historique);
    }
}
