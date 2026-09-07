package com.agriculture.backend.service;

import com.agriculture.backend.config.JwtUtil;
import com.agriculture.backend.dto.AuthResponse;
import com.agriculture.backend.dto.LoginRequest;
import com.agriculture.backend.dto.OtpRequest;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.repository.UtilisateurRepository;
import com.agriculture.backend.repository.AgriculteurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final AgriculteurRepository agriculteurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final ResendMailService resendMailService;

    @Autowired
    public AuthService(
            UtilisateurRepository utilisateurRepository,
            AgriculteurRepository agriculteurRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            JwtUtil jwtUtil,
            ResendMailService resendMailService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.agriculteurRepository = agriculteurRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.resendMailService = resendMailService;
    }

    public AuthResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!utilisateur.isActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        String role = utilisateur.getRoles()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun rôle attribué"))
                .getNom()
                .name();

        if ("AGRICULTEUR".equals(role)) {
            Agriculteur agriculteur = agriculteurRepository.findByUtilisateur(utilisateur).orElse(null);

            if (agriculteur != null && !agriculteur.isActif()) {
                return new AuthResponse(
                    "Votre inscription est réussie, mais votre compte est en attente d'activation par un Agent de terrain.",
                    null,
                    false,
                    role,
                    "EN_ATTENTE"
                );
            }
        }

        if (utilisateur.isDoubleAuthentification()) {
            try {
                envoyerOtp(utilisateur);
            } catch (Exception e) {
                return new AuthResponse("ERREUR DEBUG: " + e.getMessage(), null, true, role);
            }
            return new AuthResponse("OTP envoyé sur votre email", null, true, role);
        }

        List<String> rolesUtilisateur = utilisateur.getRoles().stream()
                .map(r -> r.getNom().name())
                .collect(Collectors.toList());

        Long regionId = (utilisateur.getRegionGeree() != null)
                ? utilisateur.getRegionGeree().getId()
                : null;
        String regionNom = (utilisateur.getRegionGeree() != null)
                ? utilisateur.getRegionGeree().getNom()
                : null;

        return new AuthResponse(
                "Connexion réussie",
                jwtUtil.genererToken(utilisateur.getEmail(), rolesUtilisateur),
                false,
                role,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                regionId,
                regionNom
        );
    }

    private void envoyerOtp(Utilisateur utilisateur) {
        String code = String.format("%06d", new Random().nextInt(999999));

        utilisateur.setCodeOtp(code);
        utilisateur.setExpirationOtp(LocalDateTime.now().plusMinutes(5));
        utilisateurRepository.save(utilisateur);

        String sujet = "Code OTP - Plateforme Agricole";
        String texte =
                "Bonjour " + utilisateur.getPrenom() + ",\n\n"
                + "Votre code OTP est : " + code + "\n\n"
                + "Ce code est valable pendant 5 minutes.";

        resendMailService.envoyer(utilisateur.getEmail(), sujet, texte);
    }

    public AuthResponse verifierOtp(OtpRequest request) {
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (utilisateur.getCodeOtp() == null || Pattern.matches("\\s*", utilisateur.getCodeOtp()) || utilisateur.getExpirationOtp() == null) {
            throw new RuntimeException("Aucun OTP généré");
        }

        if (LocalDateTime.now().isAfter(utilisateur.getExpirationOtp())) {
            throw new RuntimeException("OTP expiré");
        }

        if (!utilisateur.getCodeOtp().equals(request.getCodeOtp())) {
            throw new RuntimeException("OTP incorrect");
        }

        utilisateur.setCodeOtp(null);
        utilisateur.setExpirationOtp(null);
        utilisateurRepository.save(utilisateur);

        String role = utilisateur.getRoles()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun rôle attribué"))
                .getNom()
                .name();

        List<String> rolesUtilisateur = utilisateur.getRoles().stream()
                .map(r -> r.getNom().name())
                .collect(Collectors.toList());

        Long regionId = (utilisateur.getRegionGeree() != null)
                ? utilisateur.getRegionGeree().getId()
                : null;
        String regionNom = (utilisateur.getRegionGeree() != null)
                ? utilisateur.getRegionGeree().getNom()
                : null;

        return new AuthResponse(
                "Connexion réussie",
                jwtUtil.genererToken(utilisateur.getEmail(), rolesUtilisateur),
                false,
                role,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                regionId,
                regionNom
        );
    }

    public AuthResponse deconnexion(String email) {
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        utilisateur.setCodeOtp(null);
        utilisateur.setExpirationOtp(null);
        utilisateurRepository.save(utilisateur);

        return new AuthResponse("Déconnexion réussie", null, false, null);
    }

    public AuthResponse inscription(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setActif(true);
        utilisateur.setDoubleAuthentification(true);
        utilisateurRepository.save(utilisateur);

        return new AuthResponse("Inscription réussie", null, false, null);
    }

    public void demanderReinitialisation(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        String token = UUID.randomUUID().toString();
        utilisateur.setResetToken(token);
        utilisateur.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
        utilisateurRepository.save(utilisateur);

        String lien = "http://localhost:5173/reinitialiser-mot-de-passe?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utilisateur.getEmail());
        message.setSubject("Réinitialisation du mot de passe - Plateforme Agricole");
        message.setText(
                "Bonjour " + utilisateur.getPrenom() + ",\n\n"
                + "Cliquez sur ce lien pour réinitialiser votre mot de passe (valide 1h) :\n"
                + lien + "\n\nSi vous n'avez pas demandé cela, ignorez cet email."
        );
        mailSender.send(message);
    }

    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Lien invalide ou expiré"));

        if (utilisateur.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ce lien a expiré, veuillez refaire une demande");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setResetToken(null);
        utilisateur.setResetTokenExpiration(null);
        utilisateurRepository.save(utilisateur);
    }

    public void renvoyerOtp(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!utilisateur.isActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        envoyerOtp(utilisateur);
    }
}