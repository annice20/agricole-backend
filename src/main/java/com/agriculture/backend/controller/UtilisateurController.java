package com.agriculture.backend.controller;
import com.agriculture.backend.dto.CreationUtilisateurRequest;
import com.agriculture.backend.dto.ProfilDTO;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.service.HistoriqueActionService;
import com.agriculture.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    private UtilisateurService utilisateurService;
    private HistoriqueActionService historiqueService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService,
                                  HistoriqueActionService historiqueService) {
        this.utilisateurService = utilisateurService;
        this.historiqueService = historiqueService;
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
                    "Utilisateur",
                    description
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public List<Utilisateur> getAll() {
        return utilisateurService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public Utilisateur getById(@PathVariable Long id) {
        return utilisateurService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ProfilDTO create(@RequestBody CreationUtilisateurRequest request,
                            Authentication authentication) {
        ProfilDTO cree = utilisateurService.creerParAdmin(request);
        tracer(authentication, "CREATION",
                "Création du compte " + cree.getPrenom() + " " + cree.getNom() +
                        " (" + request.getRoleName() + ")");
        return cree;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public Utilisateur update(@PathVariable Long id, @RequestBody Utilisateur utilisateur,
                               Authentication authentication) {
        utilisateur.setId(id);
        Utilisateur modifie = utilisateurService.update(utilisateur);
        tracer(authentication, "MODIFICATION",
                "Modification du compte #" + id + " (" + modifie.getEmail() + ")");
        return modifie;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public void delete(@PathVariable Long id, Authentication authentication) {
        Utilisateur cible = utilisateurService.getById(id);
        String description = "Suppression du compte " + cible.getPrenom() + " " + cible.getNom() +
                " (" + cible.getEmail() + ")";
        utilisateurService.deleteById(id);
        tracer(authentication, "SUPPRESSION", description);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public Utilisateur changerRole(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                    Authentication authentication) {
        String roleName = (String) body.get("roleName");
        Long regionId = body.get("regionId") != null
                ? Long.valueOf(body.get("regionId").toString())
                : null;

        Utilisateur avant = utilisateurService.getById(id);
        String ancienRole = avant.getRoles().stream()
                .findFirst()
                .map(r -> r.getNom().name())
                .orElse("INCONNU");

        Utilisateur modifie = utilisateurService.changerRole(id, roleName, regionId);

        String descriptionRegion = modifie.getRegionGeree() != null
                ? " (région : " + modifie.getRegionGeree().getNom() + ")"
                : "";

        tracer(authentication, "CHANGEMENT_ROLE",
                "Changement de rôle de " + modifie.getPrenom() + " " + modifie.getNom() +
                        " : " + ancienRole + " → " + roleName + descriptionRegion);

        return modifie;
    }
}