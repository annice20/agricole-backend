package com.agriculture.backend.controller;

import com.agriculture.backend.repository.AgriculteurRepository;
import com.agriculture.backend.repository.ProgrammeAideRepository;
import com.agriculture.backend.repository.RegionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicStatsController {

    private final AgriculteurRepository agriculteurRepository;
    private final ProgrammeAideRepository programmeRepository;
    private final RegionRepository regionRepository;

    public PublicStatsController(
            AgriculteurRepository agriculteurRepository,
            ProgrammeAideRepository programmeRepository,
            RegionRepository regionRepository) {
        this.agriculteurRepository = agriculteurRepository;
        this.programmeRepository = programmeRepository;
        this.regionRepository = regionRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getPublicStats() {
        return Map.of(
            "totalAgriculteurs", agriculteurRepository.count(),
            "agriculteursActifs", agriculteurRepository.countByActifTrue(),
            "programmesActifs", programmeRepository.countByActifTrue(),
            "regionsCount", regionRepository.count()
        );
    }
}