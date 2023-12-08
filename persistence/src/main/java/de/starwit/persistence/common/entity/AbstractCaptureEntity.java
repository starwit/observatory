package de.starwit.persistence.common.entity;

import java.time.Instant;

public interface AbstractCaptureEntity {

    public Instant getCaptureTs();

    public void setCaptureTs(Instant captureTs);

}
