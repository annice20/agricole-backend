package com.agriculture.backend.repository;

import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByResetToken(String resetToken);
    List<Utilisateur> findByRoles_Nom(RoleName nom);
    List<Utilisateur> findByRoles_NomAndRegionGeree_Id(RoleName nom, Long regionId);
}