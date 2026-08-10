package com.agriculture.backend.service;

import com.agriculture.backend.model.ProgrammeAide;
import com.agriculture.backend.repository.ProgrammeAideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgrammeAideService {

    private final ProgrammeAideRepository programmeRepository;
    
    @Autowired
    public ProgrammeAideService(ProgrammeAideRepository programmeRepository) {
        this.programmeRepository = programmeRepository;
    }

    public List<ProgrammeAide> getAll() {
        return programmeRepository.findAll();
    }

    public ProgrammeAide getById(Long id) {
        return programmeRepository.findById(id).orElseThrow();
    }

    public ProgrammeAide create(ProgrammeAide programme) {
        return programmeRepository.save(programme);
    }

    public ProgrammeAide update(ProgrammeAide programme) {
        return programmeRepository.save(programme);
    }

    public void deleteById(Long id) {
        programmeRepository.deleteById(id);
    }
}