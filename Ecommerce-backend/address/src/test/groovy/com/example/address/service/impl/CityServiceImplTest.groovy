package com.example.address.service.impl
import com.example.address.entity.Address
import com.example.address.entity.City
import com.example.address.entity.Country
import com.example.address.exception.CityAlreadyExistsException
import com.example.address.exception.CityNotFoundException
import com.example.address.repository.CityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class CityServiceImplTest extends Specification {

    private CityRepository cityRepository = Mock(CityRepository)
    private CityServiceImpl cityService = new CityServiceImpl(cityRepository: cityRepository)

    def "Get all cities"() {
        given:
        Pageable pageable = Pageable.unpaged()
        List<City> cities = [new City(), new City()]
        Page<City> expectedPage = new PageImpl<>(cities, pageable, cities.size())

        when:
        Page<City> actualPage = cityService.getCities(pageable)

        then:
        1 * cityRepository.findAll(pageable) >> expectedPage
        actualPage == expectedPage
        for(int i = 0; i < cities.size(); i++) {
            assertEquals(cities[i], actualPage.getContent()[i])
        }
    }

    def "Get city by ID"() {
        given:
        String cityId = "1234"
        City city = new City(cityId: cityId, city: "Test City")

        when:
        City foundCity = cityService.getCityById(cityId)

        then:
        1 * cityRepository.findById(cityId) >> Optional.of(city)
        foundCity == city
        assertEquals(city, foundCity)
    }

    def "Get city by ID not found"() {
        given:
        String cityId = "1234"

        when:
        cityService.getCityById(cityId)

        then:
        1 * cityRepository.findById(cityId) >> Optional.empty()
        thrown(CityNotFoundException)
    }

    def "Create new city"() {
        given:
        City city = new City(cityId: null, city: "Test City")
        Country country = new Country(countryId: "123", country: "Test Country")
        city.setCountry(country)

        when:
        City createdCity = cityService.createCity(city)

        then:
        1 * cityRepository.saveAndFlush(city) >> city
        createdCity.getCityId() != null
    }

    def "Create city with existing ID"() {
        given:
        City city = new City(cityId: "1234", city: "Test City")
        Country country = new Country(countryId: "123", country: "Test Country")
        city.setCountry(country)
        City expectedCity = new City(cityId: "1234", city: "Test City", country: country)

        when:
        cityService.createCity(city)

        then:
        1 * cityRepository.findById(city.getCityId()) >> Optional.empty()
        1 * cityRepository.saveAndFlush(city) >> expectedCity
        assertEquals(expectedCity, city)
    }

    def "Create city that already exists"() {
        given:
        City city = new City(cityId: "1234", city: "Test City")
        Country country = new Country(countryId: "123", country: "Test Country")
        city.setCountry(country)

        when:
        cityService.createCity(city)

        then:
        1 * cityRepository.findById(city.getCityId()) >> Optional.of(city)
        thrown(CityAlreadyExistsException)
    }

    def "Update city"() {
        given:
        String cityId = "1234"
        City existingCity = new City(cityId: cityId, city: "Existing City")
        City updatedCity = new City(cityId: cityId, city: "Updated City")
        Country country = new Country(countryId: "123", country: "Test Country")
        updatedCity.setCountry(country)

        when:
        City returnedCity = cityService.updateCity(updatedCity)

        then:
        1 * cityRepository.existsById(cityId) >> true
        1 * cityRepository.saveAndFlush(updatedCity) >> updatedCity
        returnedCity == updatedCity
        assertEquals(updatedCity, returnedCity)
    }

    def "Update non existing city"() {
        given:
        String cityId = "1234"
        City updatedCity = new City(cityId: cityId, city: "Updated City")
        Country country = new Country(countryId: "123", country: "Test Country")
        updatedCity.setCountry(country)

        when:
        cityService.updateCity(updatedCity)

        then:
        1 * cityRepository.existsById(cityId) >> false
        thrown(CityNotFoundException)
    }

    def "Patch city"() {
        given:
        String cityId = "1234"
        City existingCity = new City(cityId: cityId, city: "Existing City")
        Map<String, Object> updates = new HashMap<>()
        updates.put("city", "Patched City")

        when:
        cityService.patchCity(cityId, updates)

        then:
        1 * cityRepository.findById(cityId) >> Optional.of(existingCity)
        1 * cityRepository.save(existingCity)
        existingCity.getCity() == "Patched City"
    }

    def "Patch non-existing city"() {
        given:
        String cityId = "1234"
        Map<String, Object> updates = new HashMap<>()
        updates.put("city", "Patched City")

        when:
        cityService.patchCity(cityId, updates)

        then:
        1 * cityRepository.findById(cityId) >> Optional.empty()
        thrown(CityNotFoundException)
    }

    def "Patch invalid field Update city"() {
        given:
        String cityId = "1234"
        City existingCity = new City(cityId: cityId, city: "Existing City")
        Map<String, Object> updates = new HashMap<>()
        updates.put("invalidField", "Patched City")

        when:
        cityService.patchCity(cityId, updates)

        then:
        1 * cityRepository.findById(cityId) >> Optional.of(existingCity)
        thrown(IllegalArgumentException)
    }

    def "Delete city"() {
        given:
        String cityId = "1234"

        when:
        cityService.deleteCity(cityId)

        then:
        1 * cityRepository.existsById(cityId) >> true
        1 * cityRepository.deleteById(cityId)
    }

    def "Delete non existing city"() {
        given:
        String cityId = "1234"

        when:
        cityService.deleteCity(cityId)

        then:
        1 * cityRepository.existsById(cityId) >> false
        thrown(CityNotFoundException)
    }

    def "Get addresses by city ID"() {
        given:
        String cityId = "1234"
        City city = new City(cityId: cityId, city: "Test City")
        List<Address> addresses = [new Address(), new Address()]
        city.setAddresses(addresses)

        when:
        List<Address> foundAddresses = cityService.getCityAddresses(cityId)

        then:
        1 * cityRepository.findById(cityId) >> Optional.of(city)
        foundAddresses == addresses
    }

    def "Get addresses by city ID not found"() {
        given:
        String cityId = "1234"

        when:
        cityService.getCityAddresses(cityId)

        then:
        1 * cityRepository.findById(cityId) >> Optional.empty()
        thrown(CityNotFoundException)
    }

    static void assertEquals(City expectedCity, City actualCity) {
        assert expectedCity.cityId == actualCity.cityId
        assert expectedCity.city == actualCity.city
        if(expectedCity.country != null && actualCity.country != null){
        assert expectedCity.country.countryId == actualCity.country.countryId
        assert expectedCity.country.country == actualCity.country.country
        }
    }
}
