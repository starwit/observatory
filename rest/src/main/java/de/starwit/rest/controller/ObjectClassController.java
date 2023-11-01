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

import de.starwit.persistence.entity.ObjectClassEntity;
import de.starwit.service.impl.ObjectClassService;
import de.starwit.persistence.exception.NotificationException;
import de.starwit.rest.exception.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * ObjectClass RestController
 * Have a look at the RequestMapping!!!!!!
 */
@RestController
@RequestMapping(path = "${rest.base-path}/objectclass")
public class ObjectClassController {

    static final Logger LOG = LoggerFactory.getLogger(ObjectClassController.class);

    @Autowired
    private ObjectClassService objectclassService;

    @Operation(summary = "Get all objectclass")
    @GetMapping
    public List<ObjectClassEntity> findAll() {
        return this.objectclassService.findAll();
    }


    @Operation(summary = "Get objectclass with id")
    @GetMapping(value = "/{id}")
    public ObjectClassEntity findById(@PathVariable("id") Long id) {
        return this.objectclassService.findById(id);
    }

    @Operation(summary = "Create objectclass")
    @PostMapping
    public ObjectClassEntity save(@Valid @RequestBody ObjectClassEntity entity) {
        return update(entity);
    }

    @Operation(summary = "Update objectclass")
    @PutMapping
    public ObjectClassEntity update(@Valid @RequestBody ObjectClassEntity entity) {
        return objectclassService.saveOrUpdate(entity);
    }

    @Operation(summary = "Delete objectclass")
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Long id) throws NotificationException {
        objectclassService.delete(id);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleException(EntityNotFoundException ex) {
        LOG.info("ObjectClass not found. {}", ex.getMessage());
        NotificationDto output = new NotificationDto("error.objectclass.notfound", "ObjectClass not found.");
        return new ResponseEntity<>(output, HttpStatus.NOT_FOUND);
    }
}
