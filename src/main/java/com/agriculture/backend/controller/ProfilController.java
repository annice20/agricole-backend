package com.agriculture.backend.controller;

import com.agriculture.backend.dto.*;
import com.agriculture.backend.service.ProfilService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profil")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @GetMapping("/{id}")
    public ProfilDTO getProfil(@PathVariable Long id) {
        return profilService.getProfil(id);
    }

    @PutMapping("/{id}")
    public ProfilDTO modifierProfil(@PathVariable Long id, @RequestBody ProfilUpdateRequest request) {
        return profilService.modifierProfil(id, request);
    }

    @PutMapping("/{id}/mot-de-passe")
    public void changerMotDePasse(@PathVariable Long id, @RequestBody ChangerMotDePasseRequest request) {
        profilService.changerMotDePasse(id, request);
    }
}