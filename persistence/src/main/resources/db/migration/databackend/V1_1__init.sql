create table analytics_job (
    id bigserial not null,
    "enabled" boolean,
    type varchar(255) check (type in ('line_crossing', 'area_occupancy')),
    name varchar(255),
    parkingareaid varchar(255),
    detectionclassid integer,
    cameraid varchar(255),
    primary key (id)
);

create table point (
    id bigserial not null,
    x float(53),
    y float(53),
    order_idx integer,
    analytics_job_id bigint,
    primary key (id)
);

alter table if exists point
    add constraint "fk_point_analytics_job"
    foreign key (analytics_job_id)
    references analytics_job;

