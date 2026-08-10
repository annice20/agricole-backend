package com.agriculture.backend.controller;
import com.agriculture.backend.dto.ReclamationDTO;
import com.agriculture.backend.model.Reclamation;
import com.agriculture.backend.service.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {
    private final ReclamationService reclamationService;
    @Autowired
    public ReclamationController(ReclamationService reclamationService) {
        this.reclamationService = reclamationService;
    }
    @GetMapping
    public List<Reclamation> getAll() {
        return reclamationService.getAll();
    }
    @GetMapping("/agriculteur/{agriculteurId}")
    public List<Reclamation> getByAgriculteur(@PathVariable Long agriculteurId) {
        return reclamationService.getByAgriculteurId(agriculteurId);
    }

    // réclamations filtrées par région (vue responsable régional)
    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getByRegion(@PathVariable Long regionId, Authentication authentication) {
        try {
            List<Reclamation> reclamations =
                    reclamationService.getByRegionId(regionId, authentication.getName());
            return ResponseEntity.ok(reclamations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping
    public Reclamation create(@RequestBody ReclamationDTO dto) {
        return reclamationService.createFromDTO(dto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reclamationService.deleteById(id);
    }

    @PutMapping("/{id}/statut")
    public Reclamation changerStatut(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String statut = body.get("statut");
        return reclamationService.changerStatut(id, statut);
    }
}