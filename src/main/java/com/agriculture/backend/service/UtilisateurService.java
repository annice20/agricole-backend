package com.agriculture.backend.service;

import com.agriculture.backend.dto.CreationUtilisateurRequest;
import com.agriculture.backend.dto.ProfilDTO;
import com.agriculture.backend.model.Region;
import com.agriculture.backend.model.Role;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.repository.RegionRepository;
import com.agriculture.backend.repository.RoleRepository;
import com.agriculture.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                               RoleRepository roleRepository,
                               RegionRepository regionRepository,
                               PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.regionRepository = regionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> getAll() {
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .noneMatch(r -> r.getNom() == RoleName.AGRICULTEUR))
                .collect(Collectors.toList());
    }

    public Utilisateur getById(Long id) {
        return utilisateurRepository.findById(id).orElseThrow();
    }

    public ProfilDTO creerParAdmin(CreationUtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        Role role = roleRepository.findByNom(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Rôle introuvable : " + request.getRoleName()));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setActif(true);
        utilisateur.setDoubleAuthentification(true);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        // Assignation de la région si fournie (cas RESPONSABLE_REGIONAL)
        if (request.getRegionId() != null) {
            Region region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new RuntimeException("Région introuvable : " + request.getRegionId()));
            utilisateur.setRegionGeree(region);
        }

        Utilisateur enregistre = utilisateurRepository.save(utilisateur);
        return versProfilDTO(enregistre);
    }

    public Utilisateur update(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public void deleteById(Long id) {
        try {
            utilisateurRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "Impossible de supprimer cet utilisateur : il est encore référencé " +
                "ailleurs dans le système (ex : fiche agriculteur, distribution, demande d'aide)."
            );
        }
    }

    private ProfilDTO versProfilDTO(Utilisateur utilisateur) {
        ProfilDTO dto = new ProfilDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setDoubleAuthentification(utilisateur.isDoubleAuthentification());
        dto.setRoles(utilisateur.getRoles().stream()
                .map(r -> r.getNom().name())
                .collect(Collectors.toList()));
        return dto;
    }

    public Utilisateur changerRole(Long id, String roleName, Long regionId) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        RoleName roleNameEnum;
        try {
            roleNameEnum = RoleName.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide : " + roleName);
        }

        Role role = roleRepository.findByNom(roleNameEnum)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable : " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        utilisateur.setRoles(roles);

        // Assignation ou retrait de la région selon le nouveau rôle
        if (roleNameEnum == RoleName.RESPONSABLE_REGIONAL) {
            if (regionId == null) {
                throw new RuntimeException("Une région doit être spécifiée pour un responsable régional");
            }
            Region region = regionRepository.findById(regionId)
                    .orElseThrow(() -> new RuntimeException("Région introuvable : " + regionId));
            utilisateur.setRegionGeree(region);
        } else {
            // Un admin ou un agent n'a pas de région gérée
            utilisateur.setRegionGeree(null);
        }

        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));
    }
}