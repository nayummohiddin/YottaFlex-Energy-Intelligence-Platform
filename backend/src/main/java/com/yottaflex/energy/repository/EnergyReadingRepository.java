package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.EnergyReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EnergyReadingRepository extends JpaRepository<EnergyReading, String> {

  @Query("SELECT SUM(e.energyKwh) FROM EnergyReading e")
  Optional<BigDecimal> totalEnergyKwh();

  @Query("SELECT MAX(e.demandKw) FROM EnergyReading e")
  Optional<BigDecimal> maxDemandKw();

  @Query("SELECT SUM(e.energyKwh) FROM EnergyReading e WHERE e.operatingState IN ('IDLE', 'OFF')")
  Optional<BigDecimal> wasteEnergyKwh();

  @Query("SELECT COUNT(e) FROM EnergyReading e WHERE e.issueType IS NOT NULL")
  Long countExceptions();

  @Query("SELECT e FROM EnergyReading e WHERE e.issueType IS NOT NULL ORDER BY e.observedAt DESC")
  List<EnergyReading> findByIssueTypeIsNotNullOrderByObservedAtDesc();
}