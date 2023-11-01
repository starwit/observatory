package de.starwit.rest.controller;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.persistence.entity.FlowEntity;
import de.starwit.service.impl.FlowService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Flow RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/flow")
public class FlowController {

    static final Logger LOG = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private FlowService flowService;

    @Operation(summary = "Get all flow")
    @GetMapping
    public List<FlowEntity> findAll() {
        return this.flowService.findAll();
    }

    @Operation(summary = "Get all flow without objectClass")
    @GetMapping(value = "/find-without-objectClass")
    public List<FlowEntity> findAllWithoutObjectClass() {
        return flowService.findAllWithoutObjectClass();
    }

    @Operation(summary = "Get all flow without other objectClass")
    @GetMapping(value = "/find-without-other-objectClass/{id}")
    public List<FlowEntity> findAllWithoutOtherObjectClass(@PathVariable("id") Long id) {
        return flowService.findAllWithoutOtherObjectClass(id);
    }

    @Operation(summary = "Get flow with id")
    @GetMapping(value = "/{id}")
    public FlowEntity findById(@PathVariable("id") Long id) {
        return this.flowService.findById(id);
    }

    @Operation(summary = "Create flow")
    @PostMapping
    public FlowEntity save(@Valid @RequestBody FlowEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update flow")
    @PutMapping
    public FlowEntity update(@Valid @RequestBody FlowEntity entity) {
        return flowService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete flow")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        flowService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("Flow not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.flow.notfound", "Flow not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
