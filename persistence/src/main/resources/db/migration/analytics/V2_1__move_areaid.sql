ALTER TABLE "metadata"
    ADD COLUMN "observation_area_id" BIGINT;

UPDATE "metadata" SET "observation_area_id" = "linecrossing"."observation_area_id"
    FROM "linecrossing" WHERE "linecrossing"."metadata_id" = "metadata"."id";

UPDATE "metadata" SET "observation_area_id" = "areaoccupancy"."observation_area_id"
    FROM "areaoccupancy" WHERE "areaoccupancy"."metadata_id" = "metadata"."id";

ALTER TABLE "linecrossing"
    DROP COLUMN IF EXISTS "observation_area_id";

ALTER TABLE "areaoccupancy"
    DROP COLUMN IF EXISTS "observation_area_id";