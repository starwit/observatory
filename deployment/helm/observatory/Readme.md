# Observatory Helm-Chart
## Considerations for running against a TimescaleDB
You can set `databases.analytics.isTimescale` if you run the `analytics` database on a TimescaleDB. This will enable additional, timescale-specific migrations (currently creating hypertables for `areaoccupancy` and `linecrossing`).\
**This should not be unset once a deployment has been switched to TimescaleDB!**\
It is, however, possible to migrate from plain Postgres to TimescaleDB.