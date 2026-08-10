package com.agriculture.backend.controller;

import com.agriculture.backend.dto.DemandeAideDTO;
import com.agriculture.backend.model.DemandeAide;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.StatutDemande;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.repository.DemandeAideRepository;
import com.agriculture.backend.service.DemandeAideService;
import com.agriculture.backend.service.HistoriqueActionService;
import com.agriculture.backend.service.UtilisateurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
public class DemandeAideController {

    private final DemandeAideService demandeService;
    private final HistoriqueActionService historiqueService;
    private final UtilisateurService utilisateurService;
    private final DemandeAideRepository demandeAideRepository;

    @Autowired
    public DemandeAideController(DemandeAideService demandeService, HistoriqueActionService historiqueService, UtilisateurService utilisateurService, DemandeAideRepository demandeAideRepository) {
        this.demandeService = demandeService;
        this.historiqueService = historiqueService;
        this.utilisateurService = utilisateurService;
        this.demandeAideRepository = demandeAideRepository;
    }

    @GetMapping
    public List<DemandeAide> getAll() {
        return demandeService.getAll();
    }

    @GetMapping("/{id}")
    public DemandeAide getById(@PathVariable Long id) {
        return demandeService.getById(id);
    }

    /**
     * Création d'une demande d'aide
     */
    @PostMapping
    public DemandeAide create(@RequestBody DemandeAideDTO dto) {
        DemandeAide nouvelleDemande = demandeService.createFromDTO(dto);
        historiqueService.enregistrerAction(
                "Utilisateur",
                "AGRICULTEUR",
                "CREATION",
                "Demande aide",
                "Création d'une nouvelle demande d'aide"
        );

        return nouvelleDemande;
    }

    /**
     * Modification d'une demande
     */
    @PutMapping("/{id}")
    public DemandeAide update(
            @PathVariable Long id,
            @RequestBody DemandeAide demande
    ) {
        demande.setId(id);

        DemandeAide modification = demandeService.update(demande);

        historiqueService.enregistrerAction(
                "Utilisateur",
                "AGRICULTEUR",
                "MODIFICATION",
                "Demande aide",
                "Modification de la demande #" + id
        );

        return modification;
    }

    /**
     * Suppression d'une demande
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        demandeService.deleteById(id);

        historiqueService.enregistrerAction(
                "Utilisateur",
                "AGRICULTEUR",
                "SUPPRESSION",
                "Demande aide",
                "Suppression de la demande #" + id
        );
    }

    /**
     * Changement du statut :
     *
     * EN_ATTENTE
     * VALIDEE
     * DISPONIBLE
     * DISTRIBUEE
     * REFUSEE
     */
    @PatchMapping("/{id}/statut")
    public DemandeAide updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        StatutDemande statut = StatutDemande.valueOf(body.get("statut"));

        DemandeAide demande = demandeService.changerStatut(id,statut);

        historiqueService.enregistrerAction(
                "Utilisateur",
                "RESPONSABLE_REGIONAL",
                "CHANGEMENT_STATUT",
                "Demande aide",
                "Modification du statut de la demande #"
                        + id
                        + " vers "
                        + statut.name()
        );

        return demande;
    }

    @GetMapping("/region/{regionId}")
    @PreAuthorize("hasRole('RESPONSABLE_REGIONAL') or hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<List<DemandeAide>> getDemandesParRegion(
            @PathVariable Long regionId,
            Authentication authentication) {

        Utilisateur user = utilisateurService.getByEmail(authentication.getName());

        boolean isAdmin = user.getRoles().stream()
            .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);

        if (!isAdmin && (user.getRegionGeree() == null
                || !user.getRegionGeree().getId().equals(regionId))) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(demandeAideRepository.findByRegionId(regionId));
    }
}