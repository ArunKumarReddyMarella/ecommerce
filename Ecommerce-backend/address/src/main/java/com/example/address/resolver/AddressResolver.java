package com.example.address.resolver;

import com.example.address.entity.Address;
import com.example.address.service.AddressService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AddressResolver implements GraphQLQueryResolver {
    @Autowired
    private AddressService addressService;

    public Page<Address> getAllAddresses(Pageable pageable) {
        return addressService.getAddresses(pageable);
    }

    public Address getAddressById(String id) {
        return addressService.getAddressById(id);
    }
}

