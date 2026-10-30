package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "energy_reading")
public class EnergyReading {
  @Id
  @Column(name = "reading_id")
  private String readingId;

  @Column(name = "meter_id")
  private String meterId;

  @Column(name = "machine_id")
  private String machineId;

  @Column(name = "observed_at")
  private OffsetDateTime observedAt;

  @Column(name = "energy_kwh")
  private BigDecimal energyKwh;

  @Column(name = "demand_kw")
  private BigDecimal demandKw;

  @Column(name = "operating_state")
  private String operatingState;

  @Column(name = "issue_type")
  private String issueType;

  public String getReadingId() { return readingId; }
  public void setReadingId(String readingId) { this.readingId = readingId; }
  public String getMeterId() { return meterId; }
  public void setMeterId(String meterId) { this.meterId = meterId; }
  public String getMachineId() { return machineId; }
  public void setMachineId(String machineId) { this.machineId = machineId; }
  public OffsetDateTime getObservedAt() { return observedAt; }
  public void setObservedAt(OffsetDateTime observedAt) { this.observedAt = observedAt; }
  public BigDecimal getEnergyKwh() { return energyKwh; }
  public void setEnergyKwh(BigDecimal energyKwh) { this.energyKwh = energyKwh; }
  public BigDecimal getDemandKw() { return demandKw; }
  public void setDemandKw(BigDecimal demandKw) { this.demandKw = demandKw; }
  public String getOperatingState() { return operatingState; }
  public void setOperatingState(String operatingState) { this.operatingState = operatingState; }
  public String getIssueType() { return issueType; }
  public void setIssueType(String issueType) { this.issueType = issueType; }
}