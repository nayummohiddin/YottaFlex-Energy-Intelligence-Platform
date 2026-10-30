# YottaFlex Energy Intelligence Platform

Synthetic industrial energy intelligence challenge solution: correlate meter telemetry, production, tariffs, weather, shifts, idle periods, and maintenance to identify waste and peak demand.

## Stack

- Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL
- React + Vite + Recharts
- Python 3.10+ generator (NumPy/Pandas)
- Docker Compose, JUnit

## Prerequisites

- Docker Desktop
- **Java 17** (JAVA_HOME must point to JDK 17; JDK 21+ will break Maven)
- Node 20+
- Python 3.10+
- psql (PostgreSQL client) — for loading generated CSVs into the database

## Quick start

### 1. Start PostgreSQL

```bash
docker compose up -d db
```

> The `docker-compose.yml` maps container port 5432 to host port **5433** (to avoid conflicts with a locally installed PostgreSQL on 5432). If port 5432 is free on your host, you can change the mapping back to `5432:5432` and update `backend/src/main/resources/application.yml` accordingly.

Verify it is healthy:

```bash
docker compose ps
```

### 2. Generate reproducible data

```bash
cd data-generator
python -m venv .venv
# Windows: .venv\Scripts\activate
# macOS/Linux: source .venv/bin/activate
pip install -r requirements.txt
python generator.py --days 90 --meters 20 --batches 2000 --seed 20261022 --out output
cd ..
```

Generated CSVs are written to `data-generator/output/`.

### 3. Load generated data into PostgreSQL

Load the CSVs into the running database (tables are created automatically by the schema mounted in `docker-compose.yml`):

```bash
PGPASSWORD=yottaflex_dev psql -h 127.0.0.1 -p 5433 -U yottaflex -d yottaflex_energy -f scripts/load_data.sql
```

A sample `scripts/load_data.sql` uses `\copy` with explicit column lists (skip `created_at` columns that have server defaults).

### 4. Start the backend

```bash
cd backend
# Ensure JAVA_HOME points to JDK 17
mvn spring-boot:run
cd ..
```

Backend starts on `http://localhost:8080`. CORS is configured to allow the Vite dev server (`http://localhost:5173` / `5174`).

### 5. Start the frontend

```bash
cd frontend
npm install
npm run dev
cd ..
```

Vite typically starts on `http://localhost:5174` (falls back if 5173 is in use).

### 6. Open the dashboard

- **Frontend:** `http://localhost:5174/` (or the port Vite reports)
- **API base:** `http://localhost:8080/api`

## Backend API endpoints

| Method  | Endpoint                           | Description                                                                                                        |
| ------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| `GET` | `/api/health`                    | Service health check                                                                                               |
| `GET` | `/api/analytics/summary`         | KPI metrics: total energy, peak demand, intensity, waste, estimated waste cost, open exceptions, potential savings |
| `GET` | `/api/analytics/exceptions`      | Data quality exceptions from loaded energy readings                                                                |
| `GET` | `/api/analytics/recommendations` | Energy-saving recommendations with evidence and estimated savings                                                  |

> The backend root `/` returns a 404 Whitelabel page by design — the service is API-only. Use the `/api/...` paths above.

## Data and ground truth

The generator creates valid baseline records, then separately labels deliberate exceptions in `ground_truth.csv`. Ground truth is for evaluation only and must not be joined into analytics input features. Seed, parameters, manifest and validation summary are written alongside generated files.

Loaded data is not persisted across container restarts unless the Docker volume `yottaflex_pg` is retained.

## Business rules

- Internally use UTC timestamps; tariff windows use plant local timezone.
- kWh is interval energy; kW is average/instantaneous demand. For a 15-minute interval, average kW = interval kWh / 0.25 hours.
- Intensity = consumed kWh / production units; omit or mark undefined for zero output.
- Waste savings = actual interval energy minus documented baseline energy, floored at zero.
- Recommendations cite their source period, evidence and calculated savings; savings are estimates, not guarantees.
- State changes are events; do not overwrite event history.

## Testing and reproducibility

Run Python validation twice with the same seed and compare the generated manifest checksums. Run backend tests with `mvn test`. Run frontend build with `npm run build`.

## Submission checklist

See `docs/submission-checklist.md`.
