package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, String> {
}