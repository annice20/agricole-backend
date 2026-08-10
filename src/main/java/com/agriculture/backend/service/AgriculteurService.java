package com.agriculture.backend.service;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import com.agriculture.backend.model.Agriculteur;
import com.agriculture.backend.model.District;
import com.agriculture.backend.model.Utilisateur;
import com.agriculture.backend.model.Role;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.model.StatutCompte;
import com.agriculture.backend.dto.AgriculteurDTO;
import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.repository.DistrictRepository;
import com.agriculture.backend.repository.UtilisateurRepository;
import com.agriculture.backend.repository.RoleRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Service
public class AgriculteurService {

    private final AgriculteurRepository agriculteurRepository;
    private final DistrictRepository districtRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // AJOUT : Pour la notification d'activation

    @Autowired
    public AgriculteurService(
            AgriculteurRepository agriculteurRepository,
            DistrictRepository districtRepository,
            UtilisateurRepository utilisateurRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender) { // AJOUT
        this.agriculteurRepository = agriculteurRepository;
        this.districtRepository = districtRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender; // AJOUT
    }

    public List<Agriculteur> getAll() {
        return agriculteurRepository.findAll();
    }

    public Agriculteur getById(Long id) {
        return agriculteurRepository.findById(id).orElseThrow();
    }

    // ============================================================
    // AJOUT : retrouve la fiche Agriculteur liée à un compte Utilisateur
    // connecté (relation @OneToOne, ids différents entre les 2 tables).
    // Utilisé par les pages agriculteur en auto-service (MesDemandes,
    // MesAides, réclamations...) pour résoudre le bon id avant tout appel.
    //
    // Note : implémentation simple via findAll() + filtre en mémoire pour
    // éviter de modifier AgriculteurRepository.java sans l'avoir sous les
    // yeux. Si les performances deviennent un sujet (grand volume de
    // données), remplacer par une requête dédiée dans le repository :
    //   Optional<Agriculteur> findByUtilisateurId(Long utilisateurId);
    // ============================================================
    public Agriculteur getByUtilisateurId(Long utilisateurId) {
        return agriculteurRepository.findAll().stream()
                .filter(a -> a.getUtilisateur() != null
                        && a.getUtilisateur().getId().equals(utilisateurId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Aucune fiche agriculteur associée à cet utilisateur"));
    }

    public Agriculteur create(Agriculteur agriculteur) {
        return agriculteurRepository.save(agriculteur);
    }

    public Agriculteur update(Agriculteur agriculteur) {
        return agriculteurRepository.save(agriculteur);
    }

    public void delete(Long id) {
        agriculteurRepository.deleteById(id);
    }

    @Transactional
    public Agriculteur createFromDto(AgriculteurDTO dto) {
        Agriculteur agriculteur = new Agriculteur();

        agriculteur.setNom(dto.getNom());
        agriculteur.setPrenom(dto.getPrenom());
        agriculteur.setCin(dto.getCin());
        agriculteur.setDateNaissance(dto.getDateNaissance());
        agriculteur.setTelephone(dto.getTelephone());
        agriculteur.setEmail(dto.getEmail());
        agriculteur.setAdresse(dto.getAdresse());
        agriculteur.setSuperficie(dto.getSuperficie());
        agriculteur.setActif(false);

        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = geometryFactory.createPoint(
                new Coordinate(dto.getLongitude(), dto.getLatitude())
            );
            point.setSRID(4326);
            agriculteur.setGeom(point);
        }

        String sexeFinal = (dto.getSexe() != null && !dto.getSexe().isBlank())
            ? dto.getSexe()
            : dto.getGenre();
        agriculteur.setSexe(sexeFinal);

        String cultureFinale = (dto.getTypeCulture() != null && !dto.getTypeCulture().isBlank())
            ? dto.getTypeCulture()
            : dto.getCulturePrincipale();
        agriculteur.setTypeCulture(cultureFinale);

        if (dto.getDistrictId() != null) {
            District dist = districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District introuvable avec l'ID : " + dto.getDistrictId()));
            agriculteur.setDistrict(dist);
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {

            if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("Un compte existe déjà avec cet email : " + dto.getEmail());
            }

            Role roleAgriculteur = roleRepository.findByNom(RoleName.AGRICULTEUR)
                .orElseThrow(() -> new RuntimeException("Rôle AGRICULTEUR introuvable en base"));

            Utilisateur nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setNom(dto.getNom());
            nouvelUtilisateur.setPrenom(dto.getPrenom());
            nouvelUtilisateur.setEmail(dto.getEmail());
            nouvelUtilisateur.setMotDePasse(passwordEncoder.encode(dto.getCin()));
            nouvelUtilisateur.setActif(true);
            nouvelUtilisateur.setRoles(Set.of(roleAgriculteur));

            utilisateurRepository.save(nouvelUtilisateur);
            agriculteur.setUtilisateur(nouvelUtilisateur);
        }

        return agriculteurRepository.save(agriculteur);
    }

    // =========================================================================
    // METHODE DE MISE A JOUR INTEGRALE + NOTIFICATION (MODIFIÉE)
    // =========================================================================
    @Transactional
    public Agriculteur updateFromDto(Long id, AgriculteurDTO dto) {
        Agriculteur existant = agriculteurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agriculteur introuvable"));

        // 1. Sauvegarde du statut d'origine pour détecter la bascule d'activation
        boolean ancienStatut = existant.isActif();

        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setCin(dto.getCin());
        existant.setDateNaissance(dto.getDateNaissance());
        existant.setTelephone(dto.getTelephone());
        existant.setEmail(dto.getEmail());
        existant.setAdresse(dto.getAdresse());
        existant.setSuperficie(dto.getSuperficie());
        existant.setActif(dto.isActif()); // Application du nouveau statut (true / false)

        String sexeFinal = (dto.getSexe() != null && !dto.getSexe().isBlank())
            ? dto.getSexe()
            : dto.getGenre();
        existant.setSexe(sexeFinal);

        String cultureFinale = (dto.getTypeCulture() != null && !dto.getTypeCulture().isBlank())
            ? dto.getTypeCulture()
            : dto.getCulturePrincipale();
        existant.setTypeCulture(cultureFinale);

        if (dto.getDistrictId() != null) {
            District dist = districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District introuvable"));
            existant.setDistrict(dist);
        }

        // 2. Alignement de l'activation du compte d'authentification lié
        if (existant.getUtilisateur() != null) {
            existant.getUtilisateur().setActif(dto.isActif());
            existant.getUtilisateur().setStatutCompte(
                dto.isActif() ? StatutCompte.ACTIF : StatutCompte.EN_ATTENTE
            );
            utilisateurRepository.save(existant.getUtilisateur());
        }

        Agriculteur misAJour = agriculteurRepository.save(existant);

        // 3. Déclenchement automatique de l'e-mail s'il passe de Inactif (false) à Actif (true)
        if (!ancienStatut && dto.isActif() && misAJour.getUtilisateur() != null) {
            try {
                envoyerEmailActivation(misAJour.getUtilisateur().getEmail(), misAJour.getPrenom());
            } catch (Exception e) {
                System.err.println("Échec de l'envoi de l'e-mail mais persistance OK : " + e.getMessage());
            }
        }

        return misAJour;
    }
    
    @Transactional
    public Agriculteur changerStatutActivation(Long id, String nouveauStatut) {
        Agriculteur agriculteur = agriculteurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agriculteur introuvable"));

        StatutCompte statut;
        try {
            statut = StatutCompte.valueOf(nouveauStatut);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut de compte invalide : " + nouveauStatut);
        }

        boolean ancienStatutActif = agriculteur.isActif();
        boolean nouveauStatutActif = (statut == StatutCompte.ACTIF);

        agriculteur.setActif(nouveauStatutActif);

        if (agriculteur.getUtilisateur() != null) {
            agriculteur.getUtilisateur().setActif(nouveauStatutActif);
            agriculteur.getUtilisateur().setStatutCompte(statut);
            utilisateurRepository.save(agriculteur.getUtilisateur());
        }

        Agriculteur misAJour = agriculteurRepository.save(agriculteur);

        // Envoi de l'email uniquement lors du passage de Inactif vers Actif
        if (!ancienStatutActif && nouveauStatutActif && misAJour.getUtilisateur() != null) {
            try {
                envoyerEmailActivation(misAJour.getUtilisateur().getEmail(), misAJour.getPrenom());
            } catch (Exception e) {
                System.err.println("Échec de l'envoi de l'e-mail mais persistance OK : " + e.getMessage());
            }
        }

        return misAJour;
    }

    public List<Agriculteur> getBeneficiairesParRegion(Long regionId) {
        return agriculteurRepository.findBeneficiairesParRegion(regionId);
    }
    
    public List<Agriculteur> getBeneficiairesParRegionSecurise(Long regionId, String emailUtilisateurConnecte) {
        Utilisateur user = utilisateurRepository.findByEmail(emailUtilisateurConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + emailUtilisateurConnecte));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);

        if (!isAdmin && (user.getRegionGeree() == null
                || !user.getRegionGeree().getId().equals(regionId))) {
            throw new RuntimeException("Accès refusé : cette région ne correspond pas à votre compte");
        }

        return agriculteurRepository.findBeneficiairesParRegion(regionId);
    }
    
    // =========================================================================
    // INSCRIPTION PUBLIQUE INTEGRALE REPRISE SANS SOUSTRACTION
    // =========================================================================
    @Transactional
    public Agriculteur inscriptionPublique(AgriculteurDTO dto) {

        if (dto.getMotDePasse() == null || dto.getMotDePasse().isBlank()) {
            throw new RuntimeException("Le mot de passe est requis pour l'inscription");
        }
        if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Un compte existe déjà avec cet email : " + dto.getEmail());
        }

        Agriculteur agriculteur = new Agriculteur();
        agriculteur.setNom(dto.getNom());
        agriculteur.setPrenom(dto.getPrenom());
        agriculteur.setCin(dto.getCin());
        agriculteur.setDateNaissance(dto.getDateNaissance());
        agriculteur.setTelephone(dto.getTelephone());
        agriculteur.setEmail(dto.getEmail());
        agriculteur.setAdresse(dto.getAdresse());
        agriculteur.setSuperficie(dto.getSuperficie());
        agriculteur.setActif(false); // Reste faux tant que l'agent n'a pas validé

        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = geometryFactory.createPoint(
                new Coordinate(dto.getLongitude(), dto.getLatitude())
            );
            point.setSRID(4326);
            agriculteur.setGeom(point);
        }

        String sexeFinal = (dto.getSexe() != null && !dto.getSexe().isBlank())
            ? dto.getSexe()
            : dto.getGenre();
        agriculteur.setSexe(sexeFinal);

        String cultureFinale = (dto.getTypeCulture() != null && !dto.getTypeCulture().isBlank())
            ? dto.getTypeCulture()
            : dto.getCulturePrincipale();
        agriculteur.setTypeCulture(cultureFinale);

        if (dto.getDistrictId() != null) {
            District dist = districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District introuvable avec l'ID : " + dto.getDistrictId()));
            agriculteur.setDistrict(dist);
        }

        Role roleAgriculteur = roleRepository.findByNom(RoleName.AGRICULTEUR)
            .orElseThrow(() -> new RuntimeException("Rôle AGRICULTEUR introuvable en base"));

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(dto.getNom());
        nouvelUtilisateur.setPrenom(dto.getPrenom());
        nouvelUtilisateur.setEmail(dto.getEmail());
        nouvelUtilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        nouvelUtilisateur.setTelephone(dto.getTelephone());
        nouvelUtilisateur.setActif(true);
        nouvelUtilisateur.setDoubleAuthentification(true);
        nouvelUtilisateur.setRoles(Set.of(roleAgriculteur));

        utilisateurRepository.save(nouvelUtilisateur);
        agriculteur.setUtilisateur(nouvelUtilisateur);

        return agriculteurRepository.save(agriculteur);
    }

    // =========================================================================
    // ENVOI DE L'EMAIL VIA SIMPLEMAILMESSAGE
    // =========================================================================
    private void envoyerEmailActivation(String emailDestinataire, String prenom) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestinataire);
        message.setSubject("Félicitations ! Votre compte Plateforme Agricole est activé");
        message.setText(
                "Bonjour " + prenom + ",\n\n"
                + "Nous avons le plaisir de vous informer que votre fiche a été vérifiée et validée par notre Agent de terrain !\n"
                + "Votre compte est désormais pleinement actif.\n\n"
                + "Vous pouvez dès à présent vous connecter pour effectuer vos demandes d'aides et de subventions.\n"
                + "Lors de votre connexion, un code OTP de sécurité vous sera envoyé.\n\n"
                + "Cordialement,\nL'équipe de la Plateforme Agricole."
        );
        mailSender.send(message);
    }
    
    public List<Agriculteur> getByRegionId(Long regionId, String emailUtilisateurConnecte) {
        Utilisateur user = utilisateurRepository.findByEmail(emailUtilisateurConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + emailUtilisateurConnecte));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getNom() == RoleName.ADMIN_NATIONAL);

        if (!isAdmin && (user.getRegionGeree() == null
                || !user.getRegionGeree().getId().equals(regionId))) {
            throw new RuntimeException("Accès refusé : cette région ne correspond pas à votre compte");
        }

        return agriculteurRepository.findByRegionId(regionId);
    }
}