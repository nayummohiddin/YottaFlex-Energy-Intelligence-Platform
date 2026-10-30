package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, String> {
  List<Machine> findByLifecycleState(String lifecycleState);
}