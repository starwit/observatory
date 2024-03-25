CREATE TABLE "linecrossing"
(
    "observation_area_id" BIGINT NOT NULL ,
    "object_id" VARCHAR(255),
    "direction" VARCHAR(255),
    "crossing_time" TIMESTAMP WITH TIME ZONE,
    "object_class_id" BIGINT,
    "metadata_id" BIGINT
);

CREATE TABLE "areaoccupancy"
(
    "observation_area_id" BIGINT NOT NULL ,
    "occupancy_time" TIMESTAMP WITH TIME ZONE NOT NULL ,
    "count" INTEGER NOT NULL ,
    "object_class_id" BIGINT,
    "metadata_id" BIGINT
);

CREATE TABLE "objectclass"
(
    "name" VARCHAR(255),
    "class_id" INTEGER,
    CONSTRAINT "objectclass_pkey" PRIMARY KEY ("class_id")
);

CREATE SEQUENCE IF NOT EXISTS "metadata_id_seq";

CREATE TABLE "metadata"
(
    "id" BIGINT NOT NULL DEFAULT nextval('metadata_id_seq'),
    "name" VARCHAR(255) NOT NULL,
    "classification" VARCHAR(255),
    "geo_referenced" BOOLEAN,
    CONSTRAINT "unique_name_classification" UNIQUE ("name", "classification")
);

CREATE SEQUENCE IF NOT EXISTS "coordinate_id_seq";

CREATE TABLE "coordinate"
(
    "id" BIGINT NOT NULL DEFAULT nextval('coordinate_id_seq'),
    "latitude" DECIMAL(22,19),
    "longitude" DECIMAL(22,19),
    "metadata_id" BIGINT
);

INSERT INTO "objectclass" ("name", "class_id") values('car', '2');