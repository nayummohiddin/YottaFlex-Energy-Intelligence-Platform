package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeterRepository extends JpaRepository<Meter, String> {
  List<Meter> findByLifecycleState(String lifecycleState);
}