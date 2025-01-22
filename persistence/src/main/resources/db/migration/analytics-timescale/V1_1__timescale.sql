SELECT create_hypertable('linecrossing', 'crossing_time', if_not_exists => TRUE);
SELECT create_hypertable('areaoccupancy', 'occupancy_time', if_not_exists => TRUE);