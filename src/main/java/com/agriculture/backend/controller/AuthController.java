package com.agriculture.backend.controller;

import com.agriculture.backend.dto.AgriculteurDTO;
import com.agriculture.backend.dto.AuthResponse;
import com.agriculture.backend.dto.LoginRequest;
import com.agriculture.backend.dto.OtpRequest;
import com.agriculture.backend.service.AgriculteurService;
import com.agriculture.backend.service.AuthService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private AgriculteurService agriculteurService;

    @Autowired
    public AuthController(AuthService authService, AgriculteurService agriculteurService) {
        this.authService = authService;
        this.agriculteurService = agriculteurService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/verifier-otp
    @PostMapping("/verifier-otp")
    public ResponseEntity<AuthResponse> verifierOtp(@RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.verifierOtp(request));
    }

    // POST /api/auth/deconnexion
    @PostMapping("/deconnexion")
    public ResponseEntity<AuthResponse> deconnexion(@RequestParam String email) {
        return ResponseEntity.ok(authService.deconnexion(email));
    }

    // POST /api/auth/inscription
    @PostMapping("/inscription")
    public ResponseEntity<AuthResponse> inscription(@RequestBody AgriculteurDTO request) {
        agriculteurService.inscriptionPublique(request);
        return ResponseEntity.ok(new AuthResponse(
            "Inscription réussie ! Votre compte sera activé après validation par un agent.",
            null, false, "AGRICULTEUR"
        ));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        authService.demanderReinitialisation(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "Email envoyé si le compte existe"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        authService.reinitialiserMotDePasse(body.get("token"), body.get("nouveauMotDePasse"));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }
    
    @PostMapping("/renvoyer-otp")
    public ResponseEntity<?> renvoyerOtp(@RequestBody Map<String, String> body) {

        authService.renvoyerOtp(body.get("email"));

        return ResponseEntity.ok(
            Map.of("message", "Un nouveau code OTP a été envoyé")
        );
    }
}