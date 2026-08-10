package com.agriculture.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Vérifier si un admin existe déjà (peu importe lequel)
        Integer nbAdmins = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM utilisateur_roles ur " +
            "JOIN roles r ON r.id = ur.role_id " +
            "WHERE r.nom = ?",
            Integer.class,
            "ADMIN_NATIONAL"
        );

        if (nbAdmins != null && nbAdmins > 0) {
            return; // amorçage déjà fait, on ne touche à rien
        }

        // Récupérer ou créer le rôle ADMIN_NATIONAL
        List<Long> roleExistant = jdbcTemplate.queryForList(
            "SELECT id FROM roles WHERE nom = ?",
            Long.class,
            "ADMIN_NATIONAL"
        );

        Long roleId;
        if (!roleExistant.isEmpty()) {
            roleId = roleExistant.get(0);
        } else {
            roleId = jdbcTemplate.queryForObject(
                "INSERT INTO roles (nom) VALUES (?) RETURNING id",
                Long.class,
                "ADMIN_NATIONAL"
            );
        }

        // Créer l'utilisateur admin par défaut
        String motDePasseHache = passwordEncoder.encode("annice");

        Long utilisateurId = jdbcTemplate.queryForObject(
        	    "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, actif, double_authentification, statut_compte) " +
        	    "VALUES (?, ?, ?, ?, true, true, ?) RETURNING id",
        	    Long.class,
        	    "NOMENJANAHARY", "Annice", "anniceflorencia@gmail.com", motDePasseHache, "ACTIF"
        );

        // Lier l'utilisateur au rôle
        jdbcTemplate.update(
            "INSERT INTO utilisateur_roles (utilisateur_id, role_id) VALUES (?, ?)",
            utilisateurId, roleId
        );

        log.warn("Admin par défaut créé automatiquement : anniceflorencia@gmail.com / annice — à changer après connexion.");
    }
}