package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, String> {
  List<Shift> findByPlantId(String plantId);
}