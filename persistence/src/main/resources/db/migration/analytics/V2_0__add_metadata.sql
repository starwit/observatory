CREATE SEQUENCE IF NOT EXISTS "metadata_id_seq";

CREATE TABLE "metadata"
(
    "id" BIGINT NOT NULL DEFAULT nextval('metadata_id_seq'),
    "name" VARCHAR(255) NOT NULL
);

ALTER TABLE "linecrossing" ADD COLUMN "metadataid" BIGINT;
ALTER TABLE "areaoccupancy" ADD COLUMN "metadataid" BIGINT;