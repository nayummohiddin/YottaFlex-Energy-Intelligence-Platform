package com.yottaflex.energy.repository;

import com.yottaflex.energy.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, String> {

  @Query("SELECT MAX(t.ratePerKwh) FROM Tariff t WHERE t.lifecycleState = 'ACTIVE'")
  Optional<BigDecimal> maxActiveRate();
}