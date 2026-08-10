package com.agriculture.backend.controller;

import com.agriculture.backend.model.ProgrammeAide;
import com.agriculture.backend.service.ProgrammeAideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/programmes")
public class ProgrammeAideController {

    private ProgrammeAideService programmeService;
    
    @Autowired
    public ProgrammeAideController(ProgrammeAideService programmeService) {
        this.programmeService = programmeService;
    }

    @GetMapping
    public List<ProgrammeAide> getAll() {
        return programmeService.getAll();
    }

    @GetMapping("/{id}")
    public ProgrammeAide getById(@PathVariable Long id) {
        return programmeService.getById(id);
    }

    @PostMapping
    public ProgrammeAide create(@RequestBody ProgrammeAide programme) {
        return programmeService.create(programme);
    }

    @PutMapping("/{id}")
    public ProgrammeAide update(@PathVariable Long id, @RequestBody ProgrammeAide programme) {
        programme.setId(id);
        return programmeService.update(programme);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        programmeService.deleteById(id);
    }
    
    @PatchMapping("/{id}")
    public ProgrammeAide patch(@PathVariable Long id, @RequestBody ProgrammeAide patch) {
        ProgrammeAide existant = programmeService.getById(id);
        if (patch.isActif() != existant.isActif()) {
            existant.setActif(patch.isActif());
        }
        return programmeService.update(existant);
    }
}