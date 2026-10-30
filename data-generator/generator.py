import argparse, hashlib, json, os
from pathlib import Path
import numpy as np
import pandas as pd
from config import PLANT_ID, PLANT_NAME, TIMEZONE, CADENCE_MINUTES
from validators import validate_data

def build(days=90, meters=20, batches=2000, seed=20261022):
    rng = np.random.default_rng(seed)
    start = pd.Timestamp("2026-01-01T00:00:00Z")
    times = pd.date_range(start, periods=days*24*60//CADENCE_MINUTES, freq=f"{CADENCE_MINUTES}min")
    plant = pd.DataFrame([{"plant_id":PLANT_ID,"name":PLANT_NAME,"timezone":TIMEZONE,"location":"Synthetic demo"}])
    machines_n = max(meters, 20)
    machines = pd.DataFrame([{"machine_id":f"MACH-{PLANT_ID}-{i:03d}","plant_id":PLANT_ID,
        "name":f"Production Machine {i:02d}","rated_power_kw":float(rng.integers(30,180)),
        "lifecycle_state":"ACTIVE"} for i in range(1,machines_n+1)])
    meter_rows=[]
    for i in range(1,meters+1):
        meter_rows.append({"meter_id":f"MTR-{PLANT_ID}-{i:03d}","plant_id":PLANT_ID,
          "meter_code":f"MTR-{PLANT_ID}-{i:03d}","energy_unit":"kWh","demand_unit":"kW",
          "cadence_minutes":CADENCE_MINUTES,"meter_total_kwh":None,"lifecycle_state":"ACTIVE"})
    meter=pd.DataFrame(meter_rows)
    mapping=[]
    for i,m in enumerate(meter.meter_id):
        machine_id=machines.machine_id.iloc[i % len(machines)]
        mapping.append({"meter_id":m,"machine_id":machine_id,"valid_from":start.isoformat(),"valid_to":None,
          "allocation_percentage":100.0,"mapping_status":"ACTIVE"})
    readings=[]
    for mi, meter_id in enumerate(meter.meter_id):
        machine_id=mapping[mi]["machine_id"]
        rated=float(machines.loc[machines.machine_id==machine_id,"rated_power_kw"].iloc[0])
        # Variable run/idle/off profiles; realistic bounded noise, deterministic seed.
        running=rng.random(len(times)) > 0.23
        idle=(~running) & (rng.random(len(times)) > 0.35)
        kw=np.where(running, rated*rng.uniform(.42,.78,len(times)), np.where(idle, rated*.08, rated*.015))
        # Mark a reproducible high demand spike.
        spike_idx=(mi*173+500) % len(times)
        kw[spike_idx]=min(rated*1.15, rated*1.05)
        energy=kw*(CADENCE_MINUTES/60)
        # deterministic meter gap, one interval
        gap_idx=(mi*211+1000) % len(times)
        for j,t in enumerate(times):
            if j==gap_idx: continue
            state="RUNNING" if running[j] else ("IDLE" if idle[j] else "OFF")
            readings.append({"reading_id":f"ENG-{meter_id}-{t.strftime('%Y%m%dT%H%M%SZ')}",
              "meter_id":meter_id,"machine_id":machine_id,"observed_at":t.isoformat(),
              "energy_kwh":round(float(energy[j]),5),"demand_kw":round(float(kw[j]),5),
              "operating_state":state,"issue_type":None})
    energy_reading=pd.DataFrame(readings)
    shift=pd.DataFrame([
      {"shift_id":"SHIFT-001","plant_id":PLANT_ID,"name":"Day","local_start":"06:00","local_end":"14:00"},
      {"shift_id":"SHIFT-002","plant_id":PLANT_ID,"name":"Evening","local_start":"14:00","local_end":"22:00"},
      {"shift_id":"SHIFT-003","plant_id":PLANT_ID,"name":"Night","local_start":"22:00","local_end":"06:00"}])
    batch_rows=[]
    for i in range(1,batches+1):
        machine_id=machines.machine_id.iloc[int(rng.integers(0,len(machines)))]
        st=times[int(rng.integers(0,len(times)-8))]
        dur=int(rng.integers(1,9))*15
        en=st+pd.Timedelta(minutes=dur)
        batch_rows.append({"batch_id":f"BATCH-2026-{i:06d}","machine_id":machine_id,
          "shift_id":shift.shift_id.iloc[int((st.hour//8)%3)],"start_time":st.isoformat(),"end_time":en.isoformat(),
          "output_qty":float(rng.integers(10,500)),"product_code":f"SKU-{int(rng.integers(1,20)):03d}","batch_state":"COMPLETED"})
    production_batch=pd.DataFrame(batch_rows)
    tariff=pd.DataFrame([
      {"tariff_id":"TAR-2026-001","plant_id":PLANT_ID,"local_timezone":TIMEZONE,"window_name":"OFF_PEAK","local_start":"22:00","local_end":"06:00","rate_per_kwh":5.0,"effective_from":"2026-01-01","effective_to":None,"lifecycle_state":"ACTIVE"},
      {"tariff_id":"TAR-2026-002","plant_id":PLANT_ID,"local_timezone":TIMEZONE,"window_name":"NORMAL","local_start":"06:00","local_end":"18:00","rate_per_kwh":7.0,"effective_from":"2026-01-01","effective_to":None,"lifecycle_state":"ACTIVE"},
      {"tariff_id":"TAR-2026-003","plant_id":PLANT_ID,"local_timezone":TIMEZONE,"window_name":"PEAK","local_start":"18:00","local_end":"22:00","rate_per_kwh":10.0,"effective_from":"2026-01-01","effective_to":None,"lifecycle_state":"ACTIVE"}])
    weather=[]
    for i,t in enumerate(times[::4]):
        weather.append({"weather_id":f"WX-{PLANT_ID}-{t.strftime('%Y%m%dT%H%MZ')}","plant_id":PLANT_ID,
          "observed_at":t.isoformat(),"temperature_c":round(float(24+7*np.sin(t.hour/24*2*np.pi)+rng.normal(0,1.5)),2),
          "humidity_pct":round(float(np.clip(55+rng.normal(0,12),0,100)),2)})
    weather_observation=pd.DataFrame(weather)
    maintenance_event=pd.DataFrame([{"event_id":"MAINT-2026-000001","machine_id":machines.machine_id.iloc[0],
      "event_type":"INSPECTION","event_time":times[len(times)//3].isoformat(),"status":"COMPLETED","notes":"Synthetic scheduled inspection"}])
    # Deliberately labelled anomalies are represented in a separate truth table.
    truth=[]
    # Duplicate reading scenario
    if len(energy_reading)>2:
        dup=energy_reading.iloc[2].to_dict()
        dup["reading_id"]=dup["reading_id"]+"-DUP"
        dup["issue_type"]="DUPLICATE_READING"
        energy_reading=pd.concat([energy_reading,pd.DataFrame([dup])],ignore_index=True)
        truth.append({"case_id":"GT-0001","issue_type":"DUPLICATE_READING","entity_id":dup["reading_id"],"expected_label":"DUPLICATE"})
    # kW/kWh confusion label case: keep numeric source intact; label the case explicitly.
    truth.append({"case_id":"GT-0002","issue_type":"UNIT_CONFUSION","entity_id":energy_reading.iloc[5].reading_id,"expected_label":"UNIT_MISMATCH"})
    truth.append({"case_id":"GT-0003","issue_type":"TARIFF_TIMEZONE_MISMATCH","entity_id":"TAR-2026-003","expected_label":"TIMEZONE_MISMATCH"})
    truth.append({"case_id":"GT-0004","issue_type":"METER_TOTAL_RECONCILIATION","entity_id":meter.iloc[0].meter_id,"expected_label":"TOTAL_MISMATCH"})
    truth.append({"case_id":"GT-0005","issue_type":"INVALID_METER_MACHINE_MAPPING","entity_id":energy_reading.iloc[10].reading_id,"expected_label":"HIERARCHY_VIOLATION"})
    data={"plant":plant,"meter":meter,"machine":machines,"meter_machine":pd.DataFrame(mapping),
      "energy_reading":energy_reading,"production_batch":production_batch,"shift":shift,"tariff":tariff,
      "weather_observation":weather_observation,"maintenance_event":maintenance_event}
    return data,pd.DataFrame(truth)

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--days",type=int,default=90); ap.add_argument("--meters",type=int,default=20)
    ap.add_argument("--batches",type=int,default=2000); ap.add_argument("--seed",type=int,default=20261022)
    ap.add_argument("--out",default="output"); args=ap.parse_args()
    out=Path(args.out); out.mkdir(parents=True,exist_ok=True)
    data,truth=build(args.days,args.meters,args.batches,args.seed)
    errors=validate_data(data)
    for name,df in data.items(): df.to_csv(out/f"{name}.csv",index=False)
    truth.to_csv(out/"ground_truth.csv",index=False)
    summary={"seed":args.seed,"days":args.days,"meters":args.meters,"batches":args.batches,
      "row_counts":{k:int(len(v)) for k,v in data.items()},"validation_errors":errors}
    (out/"validation_summary.json").write_text(json.dumps(summary,indent=2))
    manifest={}
    for p in sorted(out.glob("*.csv")):
        manifest[p.name]=hashlib.sha256(p.read_bytes()).hexdigest()
    (out/"source_manifest.json").write_text(json.dumps(manifest,indent=2))
    print(json.dumps(summary,indent=2))
    if errors: raise SystemExit(1)
if __name__=="__main__": main()
