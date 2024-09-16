package com.example.address.service;

import com.example.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AddressService {
    Page<Address> getAddresses(Pageable pageable);
    Address getAddressById(String id);
    Address createAddress(Address address);
    Address updateAddress(Address address);
    void patchAddress(String id, Map<String, Object> updates);
    void deleteAddress(String id);
}

