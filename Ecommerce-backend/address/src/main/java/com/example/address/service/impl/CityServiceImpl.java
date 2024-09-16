package com.example.address.service.impl;

import com.example.address.entity.City;
import com.example.address.repository.CityRepository;
import com.example.address.service.CityService;
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
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public Page<City> getCities(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }

    @Override
    public City getCityById(String id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        return optionalCity.orElse(null);
    }

    @Override
    public City createCity(City city) {
        if (city.getCityId() == null)
            city.setCityId(UUID.randomUUID().toString());
        else {
            Optional<City> existingCity = cityRepository.findById(city.getCityId());
            if (existingCity.isPresent()) {
                throw new RuntimeException("City with ID " + city.getCityId() + " already exists.");
            }
        }
        return cityRepository.saveAndFlush(city);
    }

    @Override
    public City updateCity(City city) {
        return cityRepository.saveAndFlush(city);
    }

    @Override
    public void patchCity(String id, Map<String, Object> updates) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = City.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingCity, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    field.set(existingCity, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        cityRepository.save(existingCity);
    }

    @Override
    public void deleteCity(String id) {
        cityRepository.deleteById(id);
    }


}

