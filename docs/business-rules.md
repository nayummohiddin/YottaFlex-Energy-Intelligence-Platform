# Business rules and evaluation

1. Master data is created before dependent records; all dependent IDs must resolve.
2. Energy readings use 15-minute cadence in baseline. Missing intervals are explicit data quality gaps, not fabricated zero consumption.
3. For interval duration `d` hours, average kW = kWh / d. A 15-minute interval means kW = kWh × 4.
4. Production intensity = kWh / output units. For zero output, intensity is null and the period is classified as no-output/idle rather than dividing by zero.
5. Tariff cost is interval energy multiplied by the tariff effective for the plant-local timestamp. Boundary-crossing intervals must be split or handled by an explicitly documented allocation.
6. High idle waste: machine is IDLE, excess over configured idle baseline persists at least 30 minutes, and not within maintenance/startup exclusion.
7. Peak demand: maximum valid kW over a selected reporting window; anomaly threshold is configurable.
8. Savings = max(actual kWh − baseline kWh, 0); estimated cost savings apply matching tariff windows.
9. Recommendation evidence includes the measurement window, baseline, actual values, source IDs and rule version.
10. Ground truth labels are separate from input and are never used to calculate features, decisions or recommendations.
11. State transitions are appended as events with timestamp, actor and from/to status.
12. Intentional anomalies carry `issue_type` and a ground-truth case ID. Unrelated rows remain valid.
