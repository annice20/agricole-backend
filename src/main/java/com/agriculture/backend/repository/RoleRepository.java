package com.agriculture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriculture.backend.model.Role;
import com.agriculture.backend.model.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNom(RoleName nom);
}