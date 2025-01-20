package com.example.address.service.impl;

import com.example.address.entity.Address;
import com.example.address.entity.City;
import com.example.address.exception.CityAlreadyExistsException;
import com.example.address.exception.CityNotFoundException;
import com.example.address.repository.CityRepository;
import com.example.address.service.CityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public Page<City> getCities(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }

    @Override
    public City getCityById(String id) {
        City optionalCity = cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("City not found"));
        return optionalCity;
    }

    @Override
    public City createCity(City city) {
        if (city.getCityId() == null)
            city.setCityId(UUID.randomUUID().toString());
        else {
            Optional<City> existingCity = cityRepository.findById(city.getCityId());
            if (existingCity.isPresent()) {
                throw new CityAlreadyExistsException("City with ID " + city.getCityId() + " already exists.");
            }
        }
        return cityRepository.saveAndFlush(city);
    }

    @Override
    public City updateCity(City city) {
        if(!cityRepository.existsById(city.getCityId()))
            throw new CityNotFoundException("City not found with ID " + city.getCityId());
        return cityRepository.saveAndFlush(city);
    }

    @Override
    public void patchCity(String id, Map<String, Object> updates) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingCity, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        cityRepository.save(existingCity);
    }

    @Override
    public void deleteCity(String id) {
        if(!cityRepository.existsById(id))
            throw new CityNotFoundException("City not found with ID " + id);
        cityRepository.deleteById(id);
    }

    @Override
    public List<Address> getCityAddresses(String id) {
        City optionalCity = cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("City not found with ID " + id));
        return optionalCity.getAddresses();
    }
    
}

