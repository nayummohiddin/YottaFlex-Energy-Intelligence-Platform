import pandas as pd

def validate_data(data):
    errors = []
    for name, frame, key in [
        ("plant", data["plant"], "plant_id"), ("meter", data["meter"], "meter_id"),
        ("machine", data["machine"], "machine_id"), ("reading", data["energy_reading"], "reading_id"),
        ("batch", data["production_batch"], "batch_id")]:
        if frame[key].duplicated().any():
            errors.append(f"{name}: duplicate primary key")
    meter_ids = set(data["meter"].meter_id)
    if not set(data["energy_reading"].meter_id).issubset(meter_ids):
        errors.append("energy_reading: invalid meter foreign key")
    machine_ids = set(data["machine"].machine_id)
    if not set(data["production_batch"].machine_id).issubset(machine_ids):
        errors.append("production_batch: invalid machine foreign key")
    if (data["energy_reading"].energy_kwh < 0).any():
        errors.append("energy_reading: negative energy")
    if (data["production_batch"].end_time <= data["production_batch"].start_time).any():
        errors.append("production_batch: invalid time ordering")
    return errors
