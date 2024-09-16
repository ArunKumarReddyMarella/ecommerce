package com.example.address.service;

import com.example.address.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CityService {
    Page<City> getCities(Pageable pageable);
    City getCityById(String id);
    City createCity(City city);
    City updateCity(City city);
    void patchCity(String id, Map<String, Object> updates);
    void deleteCity(String id);


}

