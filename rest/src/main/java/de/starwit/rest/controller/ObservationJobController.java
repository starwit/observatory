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

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.databackend.ObservationJobService;

@RestController
@RequestMapping("${rest.base-path}/observation-job")
public class ObservationJobController {

    @Autowired
    private ObservationJobService observationJobService;

    @GetMapping("/all")
    public ResponseEntity<List<ObservationJobEntity>> getAllJobs() {
        return new ResponseEntity<>(observationJobService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservationJobEntity> getJob(@PathVariable long id) {
        ObservationJobEntity jobEntity = observationJobService.findById(id);
        if (jobEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jobEntity, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ObservationJobEntity> postJob(@RequestBody ObservationJobEntity job) {
        if (job.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field 'id' must not be set.");
        }
        ObservationJobEntity newEntity = observationJobService.saveNew(job);
        return new ResponseEntity<>(newEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObservationJobEntity> updateJob(@PathVariable Long id, @RequestBody ObservationJobEntity job) {
        if (observationJobService.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ObservationJobEntity updatedEntity = observationJobService.update(id, job);
        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        if (observationJobService.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        observationJobService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> clearJobs() {
        observationJobService.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/by-observation-area/{observationAreaId}")
    public ResponseEntity<String> clearJobsByObservationAreaId(@PathVariable Long observationAreaId) {
        observationJobService.deleteByObservationAreaId(observationAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
