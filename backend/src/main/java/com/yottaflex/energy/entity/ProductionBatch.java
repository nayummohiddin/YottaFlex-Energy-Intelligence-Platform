package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "production_batch")
public class ProductionBatch {
  @Id
  @Column(name = "batch_id")
  private String batchId;

  @Column(name = "machine_id")
  private String machineId;

  @Column(name = "shift_id")
  private String shiftId;

  @Column(name = "start_time")
  private OffsetDateTime startTime;

  @Column(name = "end_time")
  private OffsetDateTime endTime;

  @Column(name = "output_qty")
  private BigDecimal outputQty;

  @Column(name = "product_code")
  private String productCode;

  @Column(name = "batch_state")
  private String batchState;

  public String getBatchId() { return batchId; }
  public void setBatchId(String batchId) { this.batchId = batchId; }
  public String getMachineId() { return machineId; }
  public void setMachineId(String machineId) { this.machineId = machineId; }
  public String getShiftId() { return shiftId; }
  public void setShiftId(String shiftId) { this.shiftId = shiftId; }
  public OffsetDateTime getStartTime() { return startTime; }
  public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
  public OffsetDateTime getEndTime() { return endTime; }
  public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
  public BigDecimal getOutputQty() { return outputQty; }
  public void setOutputQty(BigDecimal outputQty) { this.outputQty = outputQty; }
  public String getProductCode() { return productCode; }
  public void setProductCode(String productCode) { this.productCode = productCode; }
  public String getBatchState() { return batchState; }
  public void setBatchState(String batchState) { this.batchState = batchState; }
}