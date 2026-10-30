package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.ProductionBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

public interface ProductionBatchRepository extends JpaRepository<ProductionBatch, String> {

  @Query("SELECT SUM(p.outputQty) FROM ProductionBatch p WHERE p.batchState = 'COMPLETED'")
  Optional<BigDecimal> totalOutputQty();
}