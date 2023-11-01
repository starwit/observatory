CREATE SEQUENCE IF NOT EXISTS "flow_id_seq";

CREATE TABLE "flow"
(
    "parkingareaid" BIGINT NOT NULL ,
    "objectid" VARCHAR(255),
    "direction" VARCHAR(255),
    "flowtime" TIMESTAMP WITH TIME ZONE,
    "objectclass_id" BIGINT,
    "id" BIGINT NOT NULL DEFAULT nextval('flow_id_seq'),
    CONSTRAINT "flow_pkey" PRIMARY KEY ("id")
);

CREATE SEQUENCE IF NOT EXISTS "objectclass_id_seq";

CREATE TABLE "objectclass"
(
    "name" VARCHAR(255),
    "classid" INTEGER,
    "id" BIGINT NOT NULL DEFAULT nextval('objectclass_id_seq'),
    CONSTRAINT "objectclass_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "flow"
    ADD CONSTRAINT "fk_flow_objectclass"
    FOREIGN KEY ("objectclass_id")
    REFERENCES "objectclass" ("id");

