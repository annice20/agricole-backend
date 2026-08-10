package com.agriculture.backend.controller;
import com.agriculture.backend.dto.AgriculteurDTO;
import com.agriculture.backend.dto.ActivationRequest;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.service.AgriculteurService;
import com.agriculture.backend.service.HistoriqueActionService;
import com.agriculture.backend.service.UtilisateurService;
import com.agriculture.backend.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agriculteurs")
public class AgriculteurController {
    private final AgriculteurService agriculteurService;
    private final HistoriqueActionService historiqueService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public AgriculteurController(
            AgriculteurService agriculteurService,
            HistoriqueActionService historiqueService,
            UtilisateurService utilisateurService) {
        this.agriculteurService = agriculteurService;
        this.historiqueService = historiqueService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Agriculteur> getAll() {
        return agriculteurService.getAll();
    }

    @GetMapping("/{id}")
    public Agriculteur getById(@PathVariable Long id) {
        return agriculteurService.getById(id);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public Agriculteur getByUtilisateurId(@PathVariable Long utilisateurId) {
        return agriculteurService.getByUtilisateurId(utilisateurId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agriculteur create(@RequestBody AgriculteurDTO dto) {
        return agriculteurService.createFromDto(dto);
    }

    @PutMapping("/{id}")
    public Agriculteur update(@PathVariable Long id, @RequestBody AgriculteurDTO dto) {
        return agriculteurService.updateFromDto(id, dto);
    }

    @PatchMapping("/{id}/activation")
    public Agriculteur changerActivation(
            @PathVariable Long id,
            @RequestBody ActivationRequest request,
            Authentication authentication) {

        Agriculteur agriculteur = agriculteurService.changerStatutActivation(id, request.getStatusCompte());

        // Traçabilité : qui a activé/désactivé quel agriculteur, et quand
        try {
            Utilisateur auteur = utilisateurService.getByEmail(authentication.getName());
            String role = auteur.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getNom().name())
                    .orElse("INCONNU");

            String actionLabel = "ACTIF".equals(request.getStatusCompte()) ? "ACTIVATION" : "DESACTIVATION";
            String description = String.format(
                    "%s du compte de l'agriculteur %s %s (id=%d)",
                    "ACTIVATION".equals(actionLabel) ? "Activation" : "Désactivation",
                    agriculteur.getPrenom(),
                    agriculteur.getNom(),
                    agriculteur.getId()
            );

            historiqueService.enregistrerAction(
                    auteur.getNom() + " " + auteur.getPrenom(),
                    role,
                    actionLabel,
                    "Agriculteur",
                    description
            );
        } catch (Exception e) {
            // La traçabilité ne doit jamais faire échouer l'action principale
            System.err.println("Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }

        return agriculteur;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        agriculteurService.delete(id);
    }

    @GetMapping("/beneficiaires/region/{regionId}")
    public ResponseEntity<?> getBeneficiairesParRegion(
            @PathVariable Long regionId,
            Authentication authentication) {
        try {
            List<Agriculteur> beneficiaires =
                    agriculteurService.getBeneficiairesParRegionSecurise(regionId, authentication.getName());
            return ResponseEntity.ok(beneficiaires);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getAgriculteursParRegion(
            @PathVariable Long regionId,
            Authentication authentication) {
        try {
            List<Agriculteur> agriculteurs =
                    agriculteurService.getByRegionId(regionId, authentication.getName());
            return ResponseEntity.ok(agriculteurs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}