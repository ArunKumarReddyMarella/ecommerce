package com.example.address.service;

import com.example.address.entity.Address;
import com.example.address.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public interface CityService {
    Page<City> getCities(Pageable pageable);
    City getCityById(String id);
    City createCity(City city);
    City updateCity(City city);
    void patchCity(String id, Map<String, Object> updates);
    void deleteCity(String id);
    List<Address> getCityAddresses(String id);

    Collection<City> getCitiesByCountryId(String id);
}

