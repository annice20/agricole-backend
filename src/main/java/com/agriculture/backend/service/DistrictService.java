package com.agriculture.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.agriculture.backend.model.District;
import com.agriculture.backend.repository.DistrictRepository;

@Service
public class DistrictService {

    private final DistrictRepository districtRepository;

    @Autowired
    public DistrictService(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    public List<District> getAll() {
        return districtRepository.findAll();
    }

    public List<District> getByRegionId(Long regionId) {
        return districtRepository.findByRegionId(regionId);
    }
}