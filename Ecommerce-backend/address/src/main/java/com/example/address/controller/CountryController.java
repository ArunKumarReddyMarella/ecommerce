package com.example.address.controller;

import com.example.address.entity.City;
import com.example.address.entity.Country;
import com.example.address.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

//    public CountryController(CountryService countryService) {
//        this.countryService = countryService;
//    }

    @GetMapping
    public ResponseEntity<Page<Country>> getCountries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "lastUpdate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Country> countries = countryService.getCountries(pageable);
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{id}/cities")
    public ResponseEntity<List<City>> getCitiesById(@PathVariable String id){
        Country country = countryService.getCountryById(id);
        if(country == null)
            return ResponseEntity.notFound().build();
        List<City> cityList = country.getCities();
        if(cityList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cityList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable String id) {
        Country country = countryService.getCountryById(id);
        return ResponseEntity.ok(country);
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        Country createdCountry = countryService.createCountry(country);
        return ResponseEntity.ok(createdCountry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable String id, @RequestBody Country country) {
        country.setCountryId(id);
        Country updatedCountry = countryService.updateCountry(country);
        if (updatedCountry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCountry);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Country> patchCountry(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Country existingCountry = countryService.getCountryById(id);
        if (existingCountry == null) {
            return ResponseEntity.notFound().build();
        }
        countryService.patchCountry(id, updates);
        Country updatedCountry = countryService.getCountryById(id);
        return ResponseEntity.ok(updatedCountry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable String id) {
        countryService.deleteCountry(id);
        return ResponseEntity.ok("Country deleted successfully!");
    }

}
