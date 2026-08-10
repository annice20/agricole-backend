package com.agriculture.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agriculture.backend.model.HistoriqueAction;
import com.agriculture.backend.service.HistoriqueActionService;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
public class HistoriqueActionController {
	
	private final HistoriqueActionService historiqueService;

    public HistoriqueActionController(HistoriqueActionService historiqueService) {
        this.historiqueService = historiqueService;
    }

    /**
     * Récupérer tout l'historique
     * Trié du plus récent au plus ancien
     */
    @GetMapping
    public ResponseEntity<List<HistoriqueAction>> getHistorique() {
        return ResponseEntity.ok(
                historiqueService.getHistorique()
        );
    }

    /**
     * Rechercher l'historique par utilisateur
     */
    @GetMapping("/utilisateur/{nom}")
    public ResponseEntity<List<HistoriqueAction>> getParUtilisateur(@PathVariable String nom) {
        return ResponseEntity.ok(
                historiqueService.rechercherParUtilisateur(nom)
        );
    }

    /**
     * Rechercher l'historique par module
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<List<HistoriqueAction>> getParModule(@PathVariable String module) {
        return ResponseEntity.ok(
                historiqueService.rechercherParModule(module)
        );
    }

    /**
     * Ajouter manuellement une action historique
     *
     * Utilisé pour les tests ou les actions
     * qui ne sont pas encore automatisées.
     */
    @PostMapping
    public ResponseEntity<HistoriqueAction> ajouter(@RequestBody HistoriqueAction historique) {
        HistoriqueAction sauvegarde = historiqueService.enregistrer(historique);
        return ResponseEntity.ok(sauvegarde);
    }

    @PostMapping("/action")
    public ResponseEntity<HistoriqueAction> enregistrerAction(@RequestBody HistoriqueAction historique) {
        HistoriqueAction nouvelleAction = historiqueService.enregistrerAction(
                        historique.getUtilisateur(),
                        historique.getRole(),
                        historique.getAction(),
                        historique.getModule(),
                        historique.getDescription()
                );

        return ResponseEntity.ok(nouvelleAction);
    }
}
