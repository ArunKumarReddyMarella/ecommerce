package com.example.address.service.impl;

import com.example.address.entity.Address;
import com.example.address.exception.AddressAlreadyExistsException;
import com.example.address.exception.AddressNotFoundException;
import com.example.address.repository.AddressRepository;
import com.example.address.service.AddressService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Page<Address> getAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    @Override
    public Address getAddressById(String id) {
        return addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + id));
    }

    @Override
    public Address createAddress(Address address) {
        if (address.getAddressId() == null)
            address.setAddressId(UUID.randomUUID().toString());
        else {
            Optional<Address> existingAddress = addressRepository.findById(address.getAddressId());
            if (existingAddress.isPresent()) {
                throw new AddressAlreadyExistsException("Address with ID " + address.getAddressId() + " already exists.");
            }
        }
        return addressRepository.saveAndFlush(address);
    }

    @Override
    public Address updateAddress(Address address) {
            Optional<Address> existingAddress = addressRepository.findById(address.getAddressId());
            if (existingAddress.isEmpty()) {
                throw new AddressNotFoundException("Address not found with ID: " + address.getAddressId());
            }
        return addressRepository.saveAndFlush(address);
    }

    @Override
    public void patchAddress(String id, Map<String, Object> updates) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + id));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingAddress, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddress(String id) {
        if(!addressRepository.existsById(id)) {
            throw new AddressNotFoundException("Address not found with ID: " + id);
        }
        addressRepository.deleteById(id);
    }
}

