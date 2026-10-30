package com.yottaflex.energy.controller;

import com.yottaflex.energy.entity.EnergyReading;
import com.yottaflex.energy.repository.EnergyReadingRepository;
import com.yottaflex.energy.repository.ProductionBatchRepository;
import com.yottaflex.energy.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

  private final EnergyReadingRepository energyReadingRepository;
  private final ProductionBatchRepository productionBatchRepository;
  private final TariffRepository tariffRepository;

  public AnalyticsController(EnergyReadingRepository energyReadingRepository,
                             ProductionBatchRepository productionBatchRepository,
                             TariffRepository tariffRepository) {
    this.energyReadingRepository = energyReadingRepository;
    this.productionBatchRepository = productionBatchRepository;
    this.tariffRepository = tariffRepository;
  }

  @GetMapping("/summary")
  public Map<String, Object> summary() {
    BigDecimal totalEnergy = energyReadingRepository.totalEnergyKwh().orElse(BigDecimal.ZERO);
    BigDecimal peakDemand = energyReadingRepository.maxDemandKw().orElse(BigDecimal.ZERO);
    BigDecimal wasteEnergy = energyReadingRepository.wasteEnergyKwh().orElse(BigDecimal.ZERO);
    BigDecimal totalOutput = productionBatchRepository.totalOutputQty().orElse(BigDecimal.ZERO);
    BigDecimal maxRate = tariffRepository.maxActiveRate().orElse(BigDecimal.ZERO);

    BigDecimal intensity = totalOutput.compareTo(BigDecimal.ZERO) > 0
        ? totalEnergy.divide(totalOutput, 4, BigDecimal.ROUND_HALF_UP)
        : BigDecimal.ZERO;

    BigDecimal estimatedWasteCost = wasteEnergy.multiply(maxRate)
        .setScale(2, BigDecimal.ROUND_HALF_UP);

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("energyKwh", totalEnergy);
    map.put("peakDemandKw", peakDemand);
    map.put("energyIntensityKwhPerUnit", intensity);
    map.put("wasteKwh", wasteEnergy);
    map.put("estimatedWasteCost", estimatedWasteCost);
    map.put("openExceptions", energyReadingRepository.countExceptions());
    map.put("potentialSavingsKwh", wasteEnergy);
    map.put("note", "Metrics computed from loaded source records in PostgreSQL.");
    return map;
  }

  @GetMapping("/exceptions")
  public List<Map<String, Object>> exceptions() {
    return energyReadingRepository.findByIssueTypeIsNotNullOrderByObservedAtDesc().stream()
        .limit(50)
        .map(e -> {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("issueType", e.getIssueType());
          m.put("entityId", e.getMeterId());
          m.put("observedAt", e.getObservedAt());
          m.put("energyKwh", e.getEnergyKwh());
          m.put("operatingState", e.getOperatingState());
          return m;
        })
        .collect(Collectors.toList());
  }

  @GetMapping("/recommendations")
  public List<Map<String, Object>> recommendations() {
    BigDecimal wasteEnergy = energyReadingRepository.wasteEnergyKwh().orElse(BigDecimal.ZERO);
    BigDecimal maxRate = tariffRepository.maxActiveRate().orElse(BigDecimal.ZERO);
    BigDecimal estimatedSavings = wasteEnergy.multiply(maxRate)
        .setScale(2, BigDecimal.ROUND_HALF_UP);

    List<Map<String, Object>> list = new ArrayList<>();
    if (wasteEnergy.compareTo(BigDecimal.ZERO) > 0) {
      Map<String, Object> rec = new LinkedHashMap<>();
      rec.put("title", "Reduce idle and off-state energy consumption");
      rec.put("expectedSavingsKwh", wasteEnergy);
      rec.put("estimatedSavingsCost", estimatedSavings);
      rec.put("evidence", Map.of(
        "source", "energy_reading table",
        "method", "Sum of energy_kwh where operating_state in (IDLE, OFF)",
        "exceptionCount", energyReadingRepository.countExceptions()
      ));
      list.add(rec);
    }
    return list;
  }
}
