package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agriculture.backend.model.District;
import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    
    List<District> findByRegionId(Long regionId);
}