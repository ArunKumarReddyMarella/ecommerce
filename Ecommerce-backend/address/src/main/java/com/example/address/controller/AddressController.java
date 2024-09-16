package com.example.address.controller;

import com.example.address.entity.Address;
import com.example.address.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<Page<Address>> getAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "lastUpdate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Address> addresses = addressService.getAddresses(pageable);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable String id) {
        Address address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return ResponseEntity.ok(createdAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable String id, @RequestBody Address address) {
        address.setAddressId(id);
        Address updatedAddress = addressService.updateAddress(address);
        if (updatedAddress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAddress);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Address> patchAddress(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Address existingAddress = addressService.getAddressById(id);
        if (existingAddress == null) {
            return ResponseEntity.notFound().build();
        }
        addressService.patchAddress(id, updates);
        Address updatedAddress = addressService.getAddressById(id);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Address deleted successfully!");
    }
}
