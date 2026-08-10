package com.agriculture.backend.repository;

import com.agriculture.backend.model.AttributionEquipement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributionEquipementRepository extends JpaRepository<AttributionEquipement, Long> {

	List<AttributionEquipement> findByAgriculteurId(Long agriculteurId);
}
