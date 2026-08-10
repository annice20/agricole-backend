package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriculture.backend.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
}