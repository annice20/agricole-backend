package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agriculture.backend.model.HistoriqueAction;

import java.util.List;

@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
	List<HistoriqueAction> findAllByOrderByDateActionDesc();

    List<HistoriqueAction> findByUtilisateurContainingIgnoreCase(String utilisateur);

    List<HistoriqueAction> findByModule(String module);
}
