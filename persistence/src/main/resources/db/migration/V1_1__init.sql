create table ANALYTICS_JOB (
    ID bigserial not null,
    ENABLED boolean,
    TYPE smallint check (TYPE between 0 and 2),
    NAME varchar(255),
    PARKINGAREAID varchar(255),
    DETECTIONCLASSID integer,
    CAMERAID varchar(255),
    primary key (ID)
);

create table POINT (
    ID bigserial not null,
    X float(53),
    Y float(53),
    ORDER_IDX integer,
    ANALYTICS_JOB_ID bigint,
    primary key (ID)
);

alter table if exists POINT
    add constraint "fk_point_analytics_job"
    foreign key (ANALYTICS_JOB_ID)
    references ANALYTICS_JOB;

create table FLOW (
    FLOWTIME timestamp(6) with time zone,
    ID bigserial not null,
    OBJECTCLASS_ID bigint,
    PARKINGAREAID bigint not null,
    DIRECTION varchar(255) check (DIRECTION in ('in','out')),
    OBJECTID varchar(255),
    primary key (ID)
);

create table OBJECTCLASS (
    CLASSID integer,
    ID bigserial not null,
    NAME varchar(255),
    primary key (ID)
);

alter table if exists FLOW
    add constraint "fk_flow_objectclass"
    foreign key (OBJECTCLASS_ID)
    references OBJECTCLASS;

