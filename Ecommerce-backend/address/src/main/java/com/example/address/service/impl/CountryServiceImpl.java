package com.example.address.service.impl;

import com.example.address.entity.Country;
import com.example.address.repository.CountryRepository;
import com.example.address.service.CountryService;
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
        Optional<Country> optionalCountry = countryRepository.findById(id);
        return optionalCountry.orElse(null);
    }

    @Override
    public Country createCountry(Country country) {
        if (country.getCountryId() == null)
            country.setCountryId(UUID.randomUUID().toString());
        else {
            Optional<Country> existingCountry = countryRepository.findById(country.getCountryId());
            if (existingCountry.isPresent()) {
                throw new RuntimeException("Country with ID " + country.getCountryId() + " already exists.");
            }
        }
        return countryRepository.saveAndFlush(country);
    }

    @Override
    public Country updateCountry(Country country) {
        return countryRepository.saveAndFlush(country);
    }

    @Override
    public void patchCountry(String id, Map<String, Object> updates) {
        Country existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Country.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingCountry, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    field.set(existingCountry, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        countryRepository.save(existingCountry);
    }

    @Override
    public void deleteCountry(String id) {
        countryRepository.deleteById(id);
    }
}
