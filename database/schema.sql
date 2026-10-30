
CREATE TABLE IF NOT EXISTS plant (
  plant_id VARCHAR(40) PRIMARY KEY, name VARCHAR(120) NOT NULL,
  timezone VARCHAR(80) NOT NULL, location VARCHAR(160), created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS meter (
  meter_id VARCHAR(80) PRIMARY KEY, plant_id VARCHAR(40) NOT NULL REFERENCES plant(plant_id),
  meter_code VARCHAR(80) UNIQUE NOT NULL, energy_unit VARCHAR(8) NOT NULL CHECK (energy_unit='kWh'),
  demand_unit VARCHAR(8) NOT NULL CHECK (demand_unit='kW'), cadence_minutes INT NOT NULL CHECK(cadence_minutes>0),
  meter_total_kwh NUMERIC(14,3), lifecycle_state VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);
CREATE TABLE IF NOT EXISTS machine (
  machine_id VARCHAR(80) PRIMARY KEY, plant_id VARCHAR(40) NOT NULL REFERENCES plant(plant_id),
  name VARCHAR(120) NOT NULL, rated_power_kw NUMERIC(12,3) NOT NULL CHECK(rated_power_kw>0),
  lifecycle_state VARCHAR(20) NOT NULL CHECK(lifecycle_state IN ('ACTIVE','IDLE','OFF','MAINTENANCE'))
);
CREATE TABLE IF NOT EXISTS meter_machine (
  meter_id VARCHAR(80) REFERENCES meter(meter_id), machine_id VARCHAR(80) REFERENCES machine(machine_id),
  valid_from TIMESTAMPTZ NOT NULL, valid_to TIMESTAMPTZ, allocation_percentage NUMERIC(5,2) NOT NULL DEFAULT 100,
  mapping_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY(meter_id,machine_id,valid_from), CHECK(valid_to IS NULL OR valid_to>valid_from),
  CHECK(allocation_percentage>0 AND allocation_percentage<=100)
);
CREATE TABLE IF NOT EXISTS shift (
  shift_id VARCHAR(40) PRIMARY KEY, plant_id VARCHAR(40) NOT NULL REFERENCES plant(plant_id),
  name VARCHAR(80) NOT NULL, local_start TIME NOT NULL, local_end TIME NOT NULL
);
CREATE TABLE IF NOT EXISTS production_batch (
  batch_id VARCHAR(40) PRIMARY KEY, machine_id VARCHAR(80) NOT NULL REFERENCES machine(machine_id),
  shift_id VARCHAR(40) NOT NULL REFERENCES shift(shift_id), start_time TIMESTAMPTZ NOT NULL,
  end_time TIMESTAMPTZ NOT NULL, output_qty NUMERIC(14,3) NOT NULL CHECK(output_qty>=0),
  product_code VARCHAR(80), batch_state VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
  CHECK(end_time>start_time)
);
CREATE TABLE IF NOT EXISTS tariff (
  tariff_id VARCHAR(40) PRIMARY KEY, plant_id VARCHAR(40) NOT NULL REFERENCES plant(plant_id),
  local_timezone VARCHAR(80) NOT NULL, window_name VARCHAR(40) NOT NULL,
  local_start TIME NOT NULL, local_end TIME NOT NULL, rate_per_kwh NUMERIC(12,5) NOT NULL CHECK(rate_per_kwh>=0),
  effective_from DATE NOT NULL, effective_to DATE, lifecycle_state VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);
CREATE TABLE IF NOT EXISTS weather_observation (
  weather_id VARCHAR(50) PRIMARY KEY, plant_id VARCHAR(40) NOT NULL REFERENCES plant(plant_id),
  observed_at TIMESTAMPTZ NOT NULL, temperature_c NUMERIC(6,2) NOT NULL, humidity_pct NUMERIC(5,2) NOT NULL CHECK(humidity_pct BETWEEN 0 AND 100)
);
CREATE TABLE IF NOT EXISTS energy_reading (
  reading_id VARCHAR(120) PRIMARY KEY, meter_id VARCHAR(80) NOT NULL REFERENCES meter(meter_id),
  machine_id VARCHAR(80) REFERENCES machine(machine_id), observed_at TIMESTAMPTZ NOT NULL,
  energy_kwh NUMERIC(14,5) NOT NULL CHECK(energy_kwh>=0), demand_kw NUMERIC(14,5) NOT NULL CHECK(demand_kw>=0),
  operating_state VARCHAR(20) NOT NULL CHECK(operating_state IN ('RUNNING','IDLE','OFF','MAINTENANCE')),
  issue_type VARCHAR(60), UNIQUE(meter_id,observed_at,reading_id)
);
CREATE INDEX IF NOT EXISTS idx_reading_meter_time ON energy_reading(meter_id,observed_at);
CREATE INDEX IF NOT EXISTS idx_reading_machine_time ON energy_reading(machine_id,observed_at);
CREATE TABLE IF NOT EXISTS maintenance_event (
  event_id VARCHAR(50) PRIMARY KEY, machine_id VARCHAR(80) NOT NULL REFERENCES machine(machine_id),
  event_type VARCHAR(60) NOT NULL, event_time TIMESTAMPTZ NOT NULL, status VARCHAR(20) NOT NULL,
  notes TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS recommendation (
  recommendation_id VARCHAR(40) PRIMARY KEY, machine_id VARCHAR(80) NOT NULL REFERENCES machine(machine_id),
  created_at TIMESTAMPTZ NOT NULL, title VARCHAR(160) NOT NULL, baseline_kwh NUMERIC(14,5) NOT NULL,
  actual_kwh NUMERIC(14,5) NOT NULL, expected_savings_kwh NUMERIC(14,5) NOT NULL CHECK(expected_savings_kwh>=0),
  estimated_savings_cost NUMERIC(14,2) NOT NULL CHECK(estimated_savings_cost>=0),
  evidence JSONB NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
);
CREATE TABLE IF NOT EXISTS recommendation_event (
  event_id VARCHAR(50) PRIMARY KEY, recommendation_id VARCHAR(40) NOT NULL REFERENCES recommendation(recommendation_id),
  event_time TIMESTAMPTZ NOT NULL, from_status VARCHAR(20), to_status VARCHAR(20) NOT NULL, actor VARCHAR(80), notes TEXT
);
CREATE TABLE IF NOT EXISTS data_quality_issue (
  issue_id VARCHAR(50) PRIMARY KEY, issue_type VARCHAR(60) NOT NULL, entity_type VARCHAR(40) NOT NULL,
  entity_id VARCHAR(120) NOT NULL, detected_at TIMESTAMPTZ NOT NULL, severity VARCHAR(20) NOT NULL,
  ground_truth_label VARCHAR(80), resolution_status VARCHAR(20) NOT NULL DEFAULT 'OPEN', resolution_notes TEXT
);
