package com.example.address.service.impl

import com.example.address.entity.Address
import com.example.address.entity.City
import com.example.address.exception.AddressAlreadyExistsException
import com.example.address.exception.AddressNotFoundException
import com.example.address.repository.AddressRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class AddressServiceImpTest extends Specification {

    private AddressRepository addressRepository = Mock(AddressRepository)
    private AddressServiceImpl addressService = new AddressServiceImpl(addressRepository: addressRepository)

    def "Get all addresses"() {
        given:
        Pageable pageable = Pageable.unpaged()
        List<Address> addresses = [new Address(), new Address()]
        Page<Address> expectedPage = new PageImpl<>(addresses, pageable, addresses.size())

        when:
        Page<Address> actualPage = addressService.getAddresses(pageable)

        then:
        1 * addressRepository.findAll(pageable) >> expectedPage
        actualPage == expectedPage
        for(int i = 0; i < addresses.size(); i++) {
            assertEquals(addresses[i], actualPage.getContent()[i])
        }
    }

    def "Get address by ID"() {
        given:
        String addressId = "1234"
        Address address = new Address(addressId: addressId, primaryAddress: "Test Address")

        when:
        Address foundAddress = addressService.getAddressById(addressId)

        then:
        1 * addressRepository.findById(addressId) >> Optional.of(address)
        foundAddress == address
        assertEquals(address, foundAddress)
    }

    def "Get address by ID not found"() {
        given:
        String addressId = "1234"

        when:
        addressService.getAddressById(addressId)

        then:
        1 * addressRepository.findById(addressId) >> Optional.empty()
        thrown(AddressNotFoundException)
    }

    def "Create address new address"() {
        given:
        Address address = new Address(addressId: null, primaryAddress: "Test Address")
        City city = new City(cityId: "123", city: "Test City")
        address.setCity(city)

        when:
        Address createdAddress = addressService.createAddress(address)

        then:
        1 * addressRepository.saveAndFlush(address) >> address
        createdAddress.getAddressId() != null
    }

    def "Create address with existing ID"() {
        given:
        Address address = new Address(addressId: "1234", primaryAddress: "Test Address")
        City city = new City(cityId: "123", city: "Test City")
        address.setCity(city)
        Address expectedAddress = new Address(addressId: "1234", primaryAddress: "Test Address", city: city)

        when:
        addressService.createAddress(address)

        then:
        1 * addressRepository.findById(address.getAddressId()) >> Optional.empty()
        1 * addressRepository.saveAndFlush(address) >> expectedAddress
        assertEquals(expectedAddress, address)
    }

    def "Create address that already exists"() {
        given:
        Address address = new Address(addressId: "1234", primaryAddress: "Test Address")
        City city = new City(cityId: "123", city: "Test City")
        address.setCity(city)

        when:
        addressService.createAddress(address)

        then:
        1 * addressRepository.findById(address.getAddressId()) >> Optional.of(address)
        thrown(AddressAlreadyExistsException)
    }

    def "Update address"() {
        given:
        String addressId = "1234"
        Address existingAddress = new Address(addressId: addressId, primaryAddress: "Existing Address")
        Address updatedAddress = new Address(addressId: addressId, primaryAddress: "Updated Address")
        City city = new City(cityId: "123", city: "Test City")
        updatedAddress.setCity(city)

        when:
        Address returnedAddress = addressService.updateAddress(updatedAddress)

        then:
        1 * addressRepository.findById(addressId) >> Optional.of(existingAddress)
        1 * addressRepository.saveAndFlush(updatedAddress) >> updatedAddress
        returnedAddress == updatedAddress
        assertEquals(updatedAddress, returnedAddress)
    }

    def "Update non existing address"() {
        given:
        String addressId = "1234"
        Address updatedAddress = new Address(addressId: addressId, primaryAddress: "Updated Address")
        City city = new City(cityId: "123", city: "Test City")
        updatedAddress.setCity(city)

        when:
        addressService.updateAddress(updatedAddress)

        then:
        1 * addressRepository.findById(addressId) >> Optional.empty()
        thrown(AddressNotFoundException)
    }

    def "Patch address"() {
        given:
        String addressId = "1234"
        Address existingAddress = new Address(addressId: addressId, primaryAddress: "Existing Address")
        Map<String, Object> updates = new HashMap<>()
        updates.put("primaryAddress", "Patched Address")

        when:
        addressService.patchAddress(addressId, updates)

        then:
        1 * addressRepository.findById(addressId) >> Optional.of(existingAddress)
        1 * addressRepository.save(existingAddress)
        existingAddress.getPrimaryAddress() == "Patched Address"
    }

    def "Patch non-existing address"() {
        given:
        String addressId = "1234"
        Map<String, Object> updates = new HashMap<>()
        updates.put("primaryAddress", "Patched Address")

        when:
        addressService.patchAddress(addressId, updates)

        then:
        1 * addressRepository.findById(addressId) >> Optional.empty()
        thrown(AddressNotFoundException)
    }

    def "Patch invalid field Update address"() {
        given:
        String addressId = "1234"
        Address existingAddress = new Address(addressId: addressId, primaryAddress: "Existing Address")
        Map<String, Object> updates = new HashMap<>()
        updates.put("invalidField", "Patched Address")

        when:
        addressService.patchAddress(addressId, updates)

        then:
        1 * addressRepository.findById(addressId) >> Optional.of(existingAddress)
        thrown(IllegalArgumentException)
    }

    def "Delete address"() {
        given:
        String addressId = "1234"

        when:
        addressService.deleteAddress(addressId)

        then:
        1 * addressRepository.existsById(addressId) >> true
        1 * addressRepository.deleteById(addressId)
    }

    def "Delete non existing address"() {
        given:
        String addressId = "1234"

        when:
        addressService.deleteAddress(addressId)

        then:
        1 * addressRepository.existsById(addressId) >> false
        thrown(AddressNotFoundException)
    }

    void assertEquals(Address expectedAddress, Address actualAddress) {
        assert expectedAddress.getAddressId() == actualAddress.getAddressId()
        assert expectedAddress.getPrimaryAddress() == actualAddress.getPrimaryAddress()
        if(expectedAddress.getCity() != null && actualAddress.getCity() != null) {
        assert expectedAddress.getCity().getCityId() == actualAddress.getCity().getCityId()
        assert expectedAddress.getCity().getCity() == actualAddress.getCity().getCity()
        }
    }
}

