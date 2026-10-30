package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "machine")
public class Machine {
  @Id
  @Column(name = "machine_id")
  private String machineId;

  @Column(name = "plant_id")
  private String plantId;

  @Column(name = "name")
  private String name;

  @Column(name = "rated_power_kw")
  private BigDecimal ratedPowerKw;

  @Column(name = "lifecycle_state")
  private String lifecycleState;

  public String getMachineId() { return machineId; }
  public void setMachineId(String machineId) { this.machineId = machineId; }
  public String getPlantId() { return plantId; }
  public void setPlantId(String plantId) { this.plantId = plantId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public BigDecimal getRatedPowerKw() { return ratedPowerKw; }
  public void setRatedPowerKw(BigDecimal ratedPowerKw) { this.ratedPowerKw = ratedPowerKw; }
  public String getLifecycleState() { return lifecycleState; }
  public void setLifecycleState(String lifecycleState) { this.lifecycleState = lifecycleState; }
}