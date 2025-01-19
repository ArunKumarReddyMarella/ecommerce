package com.example.address.service.impl;

import com.example.address.entity.Country;
import com.example.address.exception.CountryAlreadyExistsException;
import com.example.address.exception.CountryNotFoundException;
import com.example.address.repository.CountryRepository;
import com.example.address.service.CountryService;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Page<Country> getCountries(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }

    @Override
    public Country getCountryById(String id) {
        Country optionalCountry = countryRepository.findById(id).orElseThrow(() -> new CountryNotFoundException("Country not found with ID: " + id));
        return optionalCountry;
    }

    @Override
    public Country createCountry(Country country) {
        if (country.getCountryId() == null)
            country.setCountryId(UUID.randomUUID().toString());
        else {
            Optional<Country> existingCountry = countryRepository.findById(country.getCountryId());
            if (existingCountry.isPresent()) {
                throw new CountryAlreadyExistsException("Country with ID " + country.getCountryId() + " already exists.");
            }
        }
        return countryRepository.saveAndFlush(country);
    }

    @Override
    public Country updateCountry(Country country) {
        if(!countryRepository.existsById(country.getCountryId()))
            throw new CountryNotFoundException("Country not found with ID: " + country.getCountryId());
        return countryRepository.saveAndFlush(country);
    }

    @Override
    public void patchCountry(String id, Map<String, Object> updates) {
        Country existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingCountry, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        countryRepository.save(existingCountry);
    }

    @Override
    public void deleteCountry(String id) {
        if(!countryRepository.existsById(id)) {
            throw new CountryNotFoundException("Country not found with ID " + id);
        }
        countryRepository.deleteById(id);
    }
}
