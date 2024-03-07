ALTER TABLE "point" ADD "longitude" Decimal(22,19);
ALTER TABLE "point" ADD "latitude" Decimal(22,19);

ALTER TABLE "analytics_job" ADD "geo_referenced" boolean;