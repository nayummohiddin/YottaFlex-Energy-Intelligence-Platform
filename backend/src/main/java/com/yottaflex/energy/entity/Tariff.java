package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "tariff")
public class Tariff {
  @Id
  @Column(name = "tariff_id")
  private String tariffId;

  @Column(name = "plant_id")
  private String plantId;

  @Column(name = "local_timezone")
  private String localTimezone;

  @Column(name = "window_name")
  private String windowName;

  @Column(name = "local_start")
  private Time localStart;

  @Column(name = "local_end")
  private Time localEnd;

  @Column(name = "rate_per_kwh")
  private BigDecimal ratePerKwh;

  @Column(name = "effective_from")
  private LocalDate effectiveFrom;

  @Column(name = "effective_to")
  private LocalDate effectiveTo;

  @Column(name = "lifecycle_state")
  private String lifecycleState;

  public String getTariffId() { return tariffId; }
  public void setTariffId(String tariffId) { this.tariffId = tariffId; }
  public String getPlantId() { return plantId; }
  public void setPlantId(String plantId) { this.plantId = plantId; }
  public String getLocalTimezone() { return localTimezone; }
  public void setLocalTimezone(String localTimezone) { this.localTimezone = localTimezone; }
  public String getWindowName() { return windowName; }
  public void setWindowName(String windowName) { this.windowName = windowName; }
  public Time getLocalStart() { return localStart; }
  public void setLocalStart(Time localStart) { this.localStart = localStart; }
  public Time getLocalEnd() { return localEnd; }
  public void setLocalEnd(Time localEnd) { this.localEnd = localEnd; }
  public BigDecimal getRatePerKwh() { return ratePerKwh; }
  public void setRatePerKwh(BigDecimal ratePerKwh) { this.ratePerKwh = ratePerKwh; }
  public LocalDate getEffectiveFrom() { return effectiveFrom; }
  public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
  public LocalDate getEffectiveTo() { return effectiveTo; }
  public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
  public String getLifecycleState() { return lifecycleState; }
  public void setLifecycleState(String lifecycleState) { this.lifecycleState = lifecycleState; }
}