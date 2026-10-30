# Architecture

Python synthetic generator → CSV and separate ground truth → validation → PostgreSQL → Spring Boot REST APIs → React dashboard. Roles represented: plant operator, maintenance/quality engineer, production planner, OT/IT administrator.

The supplied code is a runnable starter scaffold. Dashboard chart data is clearly marked demo-only until repository-backed aggregation is connected. Analytics endpoints currently provide response contracts and placeholders; do not describe them as fully implemented production analytics until the data access and tests are completed.
