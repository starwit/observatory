package de.starwit.service.jobs;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeMessageDto;

public interface JobInterface {

    ObservationJobEntity getConfigEntity();

    void processNewMessage(SaeMessageDto dto);

}