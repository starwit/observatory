CREATE TABLE "linecrossing"
(
    "parkingareaid" BIGINT NOT NULL ,
    "objectid" VARCHAR(255),
    "direction" VARCHAR(255),
    "crossingtime" TIMESTAMP WITH TIME ZONE,
    "objectclassid" BIGINT
);

CREATE TABLE "objectclass"
(
    "name" VARCHAR(255),
    "classid" INTEGER,
    CONSTRAINT "objectclass_pkey" PRIMARY KEY ("classid")
);

CREATE TABLE "areaoccupancy"
(
    "parkingareaid" BIGINT NOT NULL ,
    "occupancytime" TIMESTAMP WITH TIME ZONE NOT NULL ,
    "count" INTEGER NOT NULL ,
    "objectclassid" BIGINT
);



