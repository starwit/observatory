CREATE SEQUENCE IF NOT EXISTS "observation_job_id_seq";

CREATE TABLE "observation_job"
(
    "name" VARCHAR(255),
    "observation_area_id" BIGINT NOT NULL,
    "detection_class_id" integer,
    "camera_id" varchar(255),
    "enabled" BOOLEAN,
    "type" VARCHAR(255),
    "classification" VARCHAR(255) DEFAULT 'Lichtschranke',
    "geo_referenced" boolean,
    "id" BIGINT NOT NULL DEFAULT nextval('observation_job_id_seq'),
    CONSTRAINT "observationjob_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "point_id_seq";

CREATE TABLE "point"
(
    "x" decimal(24,19),
    "y" decimal(24,19),
    "longitude" decimal(22,19),
    "latitude" decimal(22,19),
    "order_idx" integer,
    "observation_job_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('point_id_seq'),
    CONSTRAINT "point_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "point"
    ADD CONSTRAINT "fk_point_observationjob"
    FOREIGN KEY ("observation_job_id")
    REFERENCES "observation_job" ("id");
