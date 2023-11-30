create table ANALYTICS_JOB (
    ID bigserial not null,
    ENABLED boolean,
    TYPE varchar(255) check (TYPE in ('LINE_CROSSING', 'AREA_OCCUPANCY')),
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

