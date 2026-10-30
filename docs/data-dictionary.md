# Data dictionary (summary)

| Entity | Field | Type | Req? | Rule / unit |
|---|---|---|---|---|
| Plant | plant_id | varchar | yes | PK, immutable |
| Plant | timezone | IANA zone | yes | valid zone, e.g. Asia/Kolkata |
| Meter | meter_id | varchar | yes | PK; `MTR-<plant>-NNN` |
| Meter | plant_id | varchar | yes | FK Plant |
| Meter | cadence_minutes | integer | yes | positive; baseline 15 |
| Meter | energy_unit | enum | yes | kWh |
| Meter | demand_unit | enum | yes | kW |
| Machine | machine_id | varchar | yes | PK |
| Machine | rated_power_kw | decimal | yes | > 0, kW |
| Machine | lifecycle_state | enum | yes | ACTIVE, IDLE, OFF, MAINTENANCE |
| EnergyReading | reading_id | varchar | yes | PK; deterministic meter/time ID |
| EnergyReading | meter_id | varchar | yes | FK Meter |
| EnergyReading | machine_id | varchar | no | FK Machine; must match active meter mapping |
| EnergyReading | observed_at | timestamptz | yes | UTC, unique meter/time expected |
| EnergyReading | energy_kwh | decimal | yes | >= 0, interval kWh |
| EnergyReading | demand_kw | decimal | yes | >= 0, kW |
| ProductionBatch | batch_id | varchar | yes | PK `BATCH-YYYY-NNNNNN` |
| ProductionBatch | machine_id | varchar | yes | FK Machine |
| ProductionBatch | shift_id | varchar | yes | FK Shift |
| ProductionBatch | output_qty | decimal | yes | >= 0, units |
| Shift | shift_id | varchar | yes | PK; plant FK |
| Shift | local_start/end | time | yes | plant-local schedule |
| Tariff | tariff_id | varchar | yes | PK `TAR-YYYY-NNN` |
| Tariff | rate_per_kwh | decimal | yes | >= 0, currency/kWh |
| Tariff | effective_from/to | date | yes/no | effective range ordered |
| WeatherObservation | weather_id | varchar | yes | PK |
| WeatherObservation | temperature_c | decimal | yes | degrees Celsius |
| WeatherObservation | humidity_pct | decimal | yes | 0–100 percent |
| MaintenanceEvent | event_id | varchar | yes | PK; machine FK |
| MaintenanceEvent | status | enum | yes | PLANNED, IN_PROGRESS, COMPLETED, CANCELLED |
| Recommendation | recommendation_id | varchar | yes | PK `REC-YYYY-NNNNNN` |
| Recommendation | expected_savings_kwh | decimal | yes | derived, >= 0 |
| Recommendation | status | enum | yes | OPEN, ACCEPTED, DISMISSED, COMPLETED |
| RecommendationEvent | event_id | varchar | yes | PK; recommendation FK |
| DataQualityIssue | issue_id | varchar | yes | PK; type/severity/status constrained by service |

Cardinality: Plant 1:N Meter, Machine, Shift, Tariff, WeatherObservation; Meter 1:N EnergyReading; Machine 1:N ProductionBatch, MaintenanceEvent, Recommendation; Meter↔Machine N:M via effective-dated MeterMachine; Recommendation 1:N RecommendationEvent. Optional: reading.machine_id can be null for plant-level meters; issue resolution details are optional. Event tables retain transitions rather than overwriting history.
