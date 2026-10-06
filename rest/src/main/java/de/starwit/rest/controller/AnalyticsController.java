package de.starwit.rest.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.analytics.AnalyticsDataService;
import de.starwit.service.observatory.ObservationJobService;

@RestController
@RequestMapping("${rest.base-path}/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsDataService analyticsDataService;

    @Autowired
    private ObservationJobService observationJobService;

    /**
     * Deletes all analytics data of a job at or after the cutoff (ISO-8601 instant, e.g. 2026-10-05T16:00:00Z)
     */
    @DeleteMapping("/job/{id}/after/{cutoffTimestamp}")
    public ResponseEntity<String> deleteJobDataAfter(@PathVariable Long id, @PathVariable Instant cutoffTimestamp) {
        ObservationJobEntity jobEntity = observationJobService.findById(id);
        if (jobEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        analyticsDataService.deleteJobDataFrom(jobEntity, cutoffTimestamp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
