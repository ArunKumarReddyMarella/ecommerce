package com.example.address.service.impl;

import com.example.address.entity.Address;
import com.example.address.repository.AddressRepository;
import com.example.address.service.AddressService;
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
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Page<Address> getAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    @Override
    public Address getAddressById(String id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        return optionalAddress.orElse(null);
    }

    @Override
    public Address createAddress(Address address) {
        if (address.getAddressId() == null)
            address.setAddressId(UUID.randomUUID().toString());
        else {
            Optional<Address> existingAddress = addressRepository.findById(address.getAddressId());
            if (existingAddress.isPresent()) {
                throw new RuntimeException("Address with ID " + address.getAddressId() + " already exists.");
            }
        }
        return addressRepository.saveAndFlush(address);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressRepository.saveAndFlush(address);
    }

    @Override
    public void patchAddress(String id, Map<String, Object> updates) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Address.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingAddress, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    field.set(existingAddress, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
}

