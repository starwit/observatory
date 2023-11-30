package de.starwit.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.starwit.persistence.databackendconfig.entity.AnalyticsJobEntity;
import de.starwit.service.impl.AnalyticsJobService;

@RestController
@RequestMapping("${rest.base-path}/analytics-job")
public class AnalyticsJobController {

    @Autowired
    private AnalyticsJobService analyticsJobService;

    @GetMapping("/all")
    public ResponseEntity<List<AnalyticsJobEntity>> getAllJobs() {
        return new ResponseEntity<>(analyticsJobService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsJobEntity> getJob(@PathVariable long id) {
        AnalyticsJobEntity jobEntity = analyticsJobService.findById(id);
        if (jobEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jobEntity, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AnalyticsJobEntity> postJob(@RequestBody AnalyticsJobEntity job) {
        if (job.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field 'id' must not be set.");
        }
        AnalyticsJobEntity newEntity = analyticsJobService.saveNew(job);
        return new ResponseEntity<>(newEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsJobEntity> updateJob(@PathVariable Long id, @RequestBody AnalyticsJobEntity job) {
        if (analyticsJobService.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        AnalyticsJobEntity updatedEntity = analyticsJobService.update(id, job);
        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        if (analyticsJobService.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        analyticsJobService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
