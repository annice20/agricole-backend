package com.agriculture.backend.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.agriculture.backend.dto.FinancementAgricoleDTO;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.service.FinancementAgricoleService;
import com.agriculture.backend.service.HistoriqueActionService;
import com.agriculture.backend.service.UtilisateurService;

@RestController
@RequestMapping("/api/financements")
public class FinancementAgricoleController {
    private final FinancementAgricoleService service;
    private final HistoriqueActionService historiqueService;
    private final UtilisateurService utilisateurService;

    public FinancementAgricoleController(
            FinancementAgricoleService service,
            HistoriqueActionService historiqueService,
            UtilisateurService utilisateurService) {
        this.service = service;
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
                    "Financement",
                    description
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }
    }

    // Consultation : ADMIN et RESPONSABLE_REGIONAL (cohérent avec MyNavbar.jsx)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL') or hasRole('RESPONSABLE_REGIONAL')")
    public ResponseEntity<List<FinancementAgricoleDTO>> obtenirTous() {
        return ResponseEntity.ok(service.obtenirTous());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL') or hasRole('RESPONSABLE_REGIONAL')")
    public ResponseEntity<FinancementAgricoleDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenirParId(id));
    }

    // Création/modification/suppression : ADMIN uniquement (décision financière)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<FinancementAgricoleDTO> creer(
            @RequestBody FinancementAgricoleDTO dto,
            Authentication authentication) {
        FinancementAgricoleDTO cree = service.creer(dto);
        tracer(authentication, "CREATION",
                "Ajout d'un financement de " + cree.getOrganisme() + " (" + cree.getMontant() + " Ar)");
        return ResponseEntity.ok(cree);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<FinancementAgricoleDTO> modifier(
            @PathVariable Long id,
            @RequestBody FinancementAgricoleDTO dto,
            Authentication authentication) {
        FinancementAgricoleDTO modifie = service.modifier(id, dto);
        tracer(authentication, "MODIFICATION",
                "Modification du financement #" + id + " (" + modifie.getOrganisme() + ")");
        return ResponseEntity.ok(modifie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id, Authentication authentication) {
        service.supprimer(id);
        tracer(authentication, "SUPPRESSION", "Suppression du financement #" + id);
        return ResponseEntity.noContent().build();
    }
}