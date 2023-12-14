INSERT INTO "analytics_job" (
    "enabled",
    "type",
    "name",
    "parkingareaid",
    "detectionclassid",
    "cameraid"
  )
VALUES (
    true,
    'AREA_OCCUPANCY',
    'AreaOccupancy',
    1,
    2,
    'RangelineSMedicalDr'
  );

INSERT INTO "analytics_job" (
    "enabled",
    "type",
    "name",
    "parkingareaid",
    "detectionclassid",
    "cameraid"
  )
VALUES (
    true,
    'LINE_CROSSING',
    'LineCrossing',
    1,
    2,
    'RangelineSMedicalDr'
  );