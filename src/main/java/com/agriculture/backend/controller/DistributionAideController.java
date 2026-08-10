package com.agriculture.backend.controller;
import com.agriculture.backend.dto.DistributionAideDTO;
import com.agriculture.backend.model.DistributionAide;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.service.DistributionAideService;
import com.agriculture.backend.service.HistoriqueActionService;
import com.agriculture.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/distributions")
public class DistributionAideController {
    private final DistributionAideService distributionService;
    private final HistoriqueActionService historiqueService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public DistributionAideController(
            DistributionAideService distributionService,
            HistoriqueActionService historiqueService,
            UtilisateurService utilisateurService
    ) {
        this.distributionService = distributionService;
        this.historiqueService = historiqueService;
        this.utilisateurService = utilisateurService;
    }

    private void tracer(Authentication authentication, String action, String description) {
        try {
            Utilisateur auteur = utilisateurService.getByEmail(authentication.getName());
            String role = auteur.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getNom().name())
                    .orElse("INCONNU");
            historiqueService.enregistrerAction(
                    auteur.getNom() + " " + auteur.getPrenom(),
                    role,
                    action,
                    "Distribution aide",
                    description
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }
    }

    @GetMapping
    public List<DistributionAide> getAll() {
        return distributionService.getAll();
    }

    @GetMapping("/{id}")
    public DistributionAide getById(@PathVariable Long id) {
        return distributionService.getById(id);
    }

    @PostMapping
    public DistributionAide create(@RequestBody DistributionAideDTO dto, Authentication authentication) {
        DistributionAide nouvelleDistribution = distributionService.createFromDTO(dto);
        tracer(authentication, "CREATION",
                "Création d'une nouvelle distribution d'aide (#" + nouvelleDistribution.getId() + ")");
        return nouvelleDistribution;
    }

    @PutMapping("/{id}")
    public DistributionAide update(@PathVariable Long id, @RequestBody DistributionAide distribution,
                                    Authentication authentication) {
        distribution.setId(id);
        DistributionAide modification = distributionService.update(distribution);
        tracer(authentication, "MODIFICATION", "Modification de la distribution #" + id);
        return modification;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        distributionService.deleteById(id);
        tracer(authentication, "SUPPRESSION", "Suppression de la distribution #" + id);
    }

    @GetMapping("/agriculteur/{agriculteurId}")
    public List<DistributionAide> getByAgriculteur(@PathVariable Long agriculteurId) {
        return distributionService.getByAgriculteur(agriculteurId);
    }

    // NOUVEAU : distributions filtrées par région (vue responsable régional)
    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getByRegion(@PathVariable Long regionId, Authentication authentication) {
        try {
            List<DistributionAide> distributions =
                    distributionService.getByRegionId(regionId, authentication.getName());
            return ResponseEntity.ok(distributions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}