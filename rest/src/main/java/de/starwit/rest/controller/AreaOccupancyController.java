package de.starwit.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.service.analytics.AreaOccupancyService;

@RestController
@RequestMapping("${rest.base-path}/area-occupancy")
public class AreaOccupancyController {

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @PutMapping("/count/{name}/{classid}/{count}")
    public ResponseEntity<AreaOccupancyEntity> setOccupancyCount(@PathVariable String name, @PathVariable int classid, @PathVariable Long count) {
        AreaOccupancyEntity entity = areaOccupancyService.setOccupancyCount(name, classid, count);
        return new ResponseEntity<AreaOccupancyEntity>(entity, HttpStatus.OK);
    }    
}
