package com.yottaflex.energy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant")
public class Plant {
  @Id
  @Column(name = "plant_id")
  private String plantId;

  @Column(name = "name")
  private String name;

  @Column(name = "timezone")
  private String timezone;

  @Column(name = "location")
  private String location;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public String getPlantId() { return plantId; }
  public void setPlantId(String plantId) { this.plantId = plantId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}