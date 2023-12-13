CREATE SEQUENCE IF NOT EXISTS "analytics_job_id_seq";

CREATE TABLE "analytics_job"
(
    "name" VARCHAR(255),
    "parkingareaid" BIGINT NOT NULL,
    "detectionclassid" integer,
    "cameraid" varchar(255),
    "enabled" BOOLEAN,
    "type" VARCHAR(255),
    "id" BIGINT NOT NULL DEFAULT nextval('analytics_job_id_seq'),
    CONSTRAINT "analyticsjob_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "point_id_seq";

CREATE TABLE "point"
(
    "x" decimal(22,19),
    "y" decimal(22,19),
    "order_idx" integer,
    "analytics_job_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('point_id_seq'),
    CONSTRAINT "point_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "point"
    ADD CONSTRAINT "fk_point_analyticsjob"
    FOREIGN KEY ("analytics_job_id")
    REFERENCES "analytics_job" ("id");
