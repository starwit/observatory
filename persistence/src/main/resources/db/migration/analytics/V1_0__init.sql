CREATE SEQUENCE IF NOT EXISTS "linecrossing_id_seq";

CREATE TABLE "linecrossing"
(
    "parkingareaid" BIGINT NOT NULL ,
    "objectid" VARCHAR(255),
    "direction" VARCHAR(255),
    "crossingtime" TIMESTAMP WITH TIME ZONE,
    "objectclass_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('linecrossing_id_seq'),
    CONSTRAINT "linecrossing_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "objectclass_id_seq";

CREATE TABLE "objectclass"
(
    "name" VARCHAR(255),
    "classid" INTEGER,
    "id" BIGINT NOT NULL DEFAULT nextval('objectclass_id_seq'),
    CONSTRAINT "objectclass_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "areaoccupancy_id_seq";

CREATE TABLE "areaoccupancy"
(
    "occupancytime" TIMESTAMP WITH TIME ZONE NOT NULL ,
    "count" INTEGER NOT NULL ,
    "objectclass_id" BIGINT UNIQUE,
    "id" BIGINT NOT NULL DEFAULT nextval('areaoccupancy_id_seq'),
    CONSTRAINT "areaoccupancy_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "linecrossing"
    ADD CONSTRAINT "fk_linecrossing_objectclass"
    FOREIGN KEY ("objectclass_id")
    REFERENCES "objectclass" ("id");

ALTER TABLE "areaoccupancy"
    ADD CONSTRAINT "fk_areaoccupancy_objectclass"
    FOREIGN KEY ("objectclass_id")
    REFERENCES "objectclass" ("id");


