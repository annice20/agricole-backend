package com.agriculture.backend.config;

import com.agriculture.backend.model.District;
import com.agriculture.backend.model.Region;
import com.agriculture.backend.model.Role;
import com.agriculture.backend.model.RoleName;
import com.agriculture.backend.repository.DistrictRepository;
import com.agriculture.backend.repository.RegionRepository;
import com.agriculture.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;

    // Injection de tous les repositories nécessaires via le constructeur
    public DataInitializer(RoleRepository roleRepository, 
                           RegionRepository regionRepository, 
                           DistrictRepository districtRepository) {
        this.roleRepository = roleRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
    }

    @Override
    @Transactional // Assure que toute l'initialisation se passe bien ou s'annule en cas d'erreur
    public void run(String... args) {
        // 1. Initialisation de vos Rôles (Votre code existant préservé)
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByNom(roleName).isEmpty()) {
                Role role = new Role();
                role.setNom(roleName);
                roleRepository.save(role);
                System.out.println("Rôle créé : " + roleName);
            }
        }

        // 2. Initialisation des Régions et Districts de Madagascar
        if (regionRepository.count() == 0) {
            System.out.println(">>> Initialisation des régions et districts de Madagascar...");

            // Cartographie officielle des 23 régions de Madagascar avec leurs districts respectifs
            Map<String, List<String>> madaData = Map.ofEntries(
                Map.entry("ALAOTRA_MANGORO", List.of("Ambatondrazaka", "Amparafaravola", "Andilamena", "Anosibe An'ala", "Moramanga")),
                Map.entry("AMORON_I_MANIA", List.of("Ambatofinandrahana", "Ambositra", "Fandriana", "Manandriana")),
                Map.entry("ANALALANJIROFO", List.of("Fenerive Est", "Mananara Nord", "Maroantsetra", "Sainte Marie", "Soanierana Ivongo", "Vavatenina")),
                Map.entry("ANALAMANGA", List.of("Antananarivo Renivohitra", "Antananarivo Atsimondrano", "Antananarivo Avaradrano", "Andramasina", "Anjozorobe", "Ankazobe", "Manjakandriana", "Ambohidratrimo")),
                Map.entry("ANDROY", List.of("Ambovombe", "Bekily", "Beloha", "Tsihombe")),
                Map.entry("ANOSY", List.of("Amboasary Atsimo", "Betroka", "Taolagnaro")),
                Map.entry("ATSINANANA", List.of("Toamasina I", "Toamasina II", "Antanambao Manampotsy", "Mahanoro", "Marolambo", "Vatomandry", "Vohibinany")),
                Map.entry("ATSIMO_ANDREFANA", List.of("Toliara I", "Toliara II", "Ampanihy", "Ankazoabo", "Benenitra", "Beroroha", "Betioky Atsimo", "Morombe")),
                Map.entry("ATSIMO_ATSINANANA", List.of("Farafangana", "Vangaindrano", "Midongy Atsimo", "Befotaka", "Vondrozo")),
                Map.entry("BOENY", List.of("Mahajanga I", "Mahajanga II", "Ambato Boeny", "Mitsinjo", "Marovoay", "Soalala")),
                Map.entry("BONGOLAVA", List.of("Fenoarivobe", "Tsiroanomandidy")),
                Map.entry("DIANA", List.of("Antsiranana I", "Antsiranana II", "Ambanja", "Ambilobe", "Nosy Be")),
                Map.entry("HAUTE_MATSIATRA", List.of("Fianarantsoa", "Ambalavao", "Ambohimahasoa", "Ikalamavony", "Isandra", "Lalangina", "Vohibato")),
                Map.entry("IHOROMBE", List.of("Ihosy", "Iakora", "Ivohibe")),
                Map.entry("ITASY", List.of("Arivonimamo", "Miarinarivo", "Soavinandriana")),
                Map.entry("MELAKY", List.of("Ambatomainty", "Antsalova", "Besalampy", "Maintirano", "Morafenobe")),
                Map.entry("MENABE", List.of("Morondava", "Belo sur Tsiribihina", "Mahabo", "Manja", "Miandrivazo")),
                Map.entry("SAVA", List.of("Sambava", "Antalaha", "Andapa", "Vohimarina")),
                Map.entry("SOFIA", List.of("Antsohihy", "Bealanana", "Befandriana Nord", "Boriziny", "Mampikony", "Mandritsara", "Analalava")),
                Map.entry("VAKINANKARATRA", List.of("Antsirabe I", "Antsirabe II", "Ambatolampy", "Betafo", "Antanifotsy", "Faratsiho", "Mandoto")),
                Map.entry("VATOVAVY", List.of("Mananjary", "Ifanadiana", "Nosy Varika")),
                Map.entry("FITOVINANY", List.of("Manakara", "Vohipeno", "Ikongo")),
                Map.entry("SAVA_NEW", List.of("Ambanja"))
            );

            // Itération pour enregistrer proprement les entités liées
            madaData.forEach((nomRegion, listeDistricts) -> {
                Region region = new Region();
                // "AN_ALAMANGA" devient "AN ALAMANGA" pour un affichage propre
                region.setNom(nomRegion.replace("_", " ")); 
                region.setCode(nomRegion.substring(0, Math.min(nomRegion.length(), 4)));
                
                // On sauvegarde d'abord la région pour obtenir son ID
                Region savedRegion = regionRepository.save(region);

                // On associe et sauvegarde chaque district à sa région
                for (String nomDistrict : listeDistricts) {
                    District district = new District();
                    district.setNom(nomDistrict);
                    district.setRegion(savedRegion);
                    districtRepository.save(district);
                }
            });

            System.out.println(">>> Toutes les régions et tous les districts ont été insérés avec succès !");
        } else {
            System.out.println(">>> Les régions existent déjà en base de données. Initialisation ignorée.");
        }
    }
}