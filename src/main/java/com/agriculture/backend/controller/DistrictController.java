package com.agriculture.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.agriculture.backend.model.District;
import com.agriculture.backend.service.DistrictService;

@RestController
@RequestMapping("/api/districts")
public class DistrictController {
	
    private DistrictService districtService;

    @Autowired
    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;
    }

    @GetMapping
    public List<District> getAll() {
        return districtService.getAll();
    }

    @GetMapping("/region/{regionId}")
    public List<District> getByRegion(@PathVariable Long regionId) {
        return districtService.getByRegionId(regionId);
    }
}