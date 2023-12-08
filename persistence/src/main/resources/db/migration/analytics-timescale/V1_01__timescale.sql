
SELECT create_hypertable('linecrossing', 'crossingtime', if_not_exists => TRUE);

SELECT create_hypertable('areaoccupancy', 'occupancytime', if_not_exists => TRUE);