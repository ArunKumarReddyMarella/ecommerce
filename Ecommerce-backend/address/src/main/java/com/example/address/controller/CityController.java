package com.example.address.controller;

import com.example.address.entity.Address;
import com.example.address.entity.City;
import com.example.address.service.CityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<Page<City>> getCities(Pageable pageable) {

        Page<City> cities = cityService.getCities(pageable);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable String id) {
        City city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        City createdCity = cityService.createCity(city);
        return ResponseEntity.ok(createdCity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable String id, @RequestBody City city) {
        city.setCityId(id);
        City updatedCity = cityService.updateCity(city);
        if (updatedCity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCity);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<City> patchCity(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        City existingCity = cityService.getCityById(id);
        if (existingCity == null) {
            return ResponseEntity.notFound().build();
        }
        cityService.patchCity(id, updates);
        City updatedCity = cityService.getCityById(id);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable String id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok("City deleted successfully!");
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<Address>> getCityAddresses(@PathVariable String id){
        List<Address> addressList = cityService.getCityAddresses(id);
        if(addressList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(addressList);
    }
}
