package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "meter")
public class Meter {
  @Id
  @Column(name = "meter_id")
  private String meterId;

  @Column(name = "plant_id")
  private String plantId;

  @Column(name = "meter_code")
  private String meterCode;

  @Column(name = "energy_unit")
  private String energyUnit;

  @Column(name = "demand_unit")
  private String demandUnit;

  @Column(name = "cadence_minutes")
  private Integer cadenceMinutes;

  @Column(name = "meter_total_kwh")
  private BigDecimal meterTotalKwh;

  @Column(name = "lifecycle_state")
  private String lifecycleState;

  public String getMeterId() { return meterId; }
  public void setMeterId(String meterId) { this.meterId = meterId; }
  public String getPlantId() { return plantId; }
  public void setPlantId(String plantId) { this.plantId = plantId; }
  public String getMeterCode() { return meterCode; }
  public void setMeterCode(String meterCode) { this.meterCode = meterCode; }
  public String getEnergyUnit() { return energyUnit; }
  public void setEnergyUnit(String energyUnit) { this.energyUnit = energyUnit; }
  public String getDemandUnit() { return demandUnit; }
  public void setDemandUnit(String demandUnit) { this.demandUnit = demandUnit; }
  public Integer getCadenceMinutes() { return cadenceMinutes; }
  public void setCadenceMinutes(Integer cadenceMinutes) { this.cadenceMinutes = cadenceMinutes; }
  public BigDecimal getMeterTotalKwh() { return meterTotalKwh; }
  public void setMeterTotalKwh(BigDecimal meterTotalKwh) { this.meterTotalKwh = meterTotalKwh; }
  public String getLifecycleState() { return lifecycleState; }
  public void setLifecycleState(String lifecycleState) { this.lifecycleState = lifecycleState; }
}