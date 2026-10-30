package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "shift")
public class Shift {
  @Id
  @Column(name = "shift_id")
  private String shiftId;

  @Column(name = "plant_id")
  private String plantId;

  @Column(name = "name")
  private String name;

  @Column(name = "local_start")
  private Time localStart;

  @Column(name = "local_end")
  private Time localEnd;

  public String getShiftId() { return shiftId; }
  public void setShiftId(String shiftId) { this.shiftId = shiftId; }
  public String getPlantId() { return plantId; }
  public void setPlantId(String plantId) { this.plantId = plantId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Time getLocalStart() { return localStart; }
  public void setLocalStart(Time localStart) { this.localStart = localStart; }
  public Time getLocalEnd() { return localEnd; }
  public void setLocalEnd(Time localEnd) { this.localEnd = localEnd; }
}