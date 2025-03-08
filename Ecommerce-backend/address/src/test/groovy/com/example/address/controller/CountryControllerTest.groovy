package com.example.address.controller


import com.example.address.entity.City
import com.example.address.entity.Country
import com.example.address.exception.CountryNotFoundException
import com.example.address.service.CountryService
import com.example.address.service.impl.CountryServiceImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class CountryControllerTest extends Specification {

//    CountryService countryService = Mock(CountryService)
//    CountryController countryController = new CountryController(countryService)

    CountryController countryController
    CountryService countryService = Mock(CountryServiceImpl)

    def setup() {
        countryController = new CountryController(countryService: countryService)
    }


    def "Get all countries"() {
        given:
        int page = 0
        int size = 10
        String sortDirection = "desc"
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "lastUpdate"))
        List<Country> countries = [new Country(), new Country()]
        Page<Country> expectedPage = new PageImpl<>(countries, pageable, countries.size())

        when:
        ResponseEntity<Page<Country>> response = countryController.getCountries(page, size, sortDirection)

        then:
        1 * countryService.getCountries(pageable) >> expectedPage
        response.statusCode == HttpStatus.OK
        response.body == expectedPage
    }

    def "Get country by ID"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Test Country")

        when:
        ResponseEntity<Country> response = countryController.getCountryById(countryId)

        then:
        1 * countryService.getCountryById(countryId) >> country
        response.statusCode == HttpStatus.OK
        response.body == country
    }

//    def "Get country by ID not found"() {
//        given:
//        def countryId = "1234"
//
//        when:
//        countryService.getCountryById(countryId) >> throw new CountryNotFoundException("Country not found with ID: $countryId")
//        ResponseEntity<Country> response = countryController.getCountryById(countryId)
//
//        then:
//        1 * countryService.getCountryById(countryId)
//        response.statusCode == HttpStatus.NOT_FOUND
//    }
    def "Get country by ID not found"() {
        given:
        def countryId = "1234"

        when:
        countryService.getCountryById(countryId) >> {throw new CountryNotFoundException("Country not found with ID: $countryId")}
        ResponseEntity<Country> response = countryController.getCountryById(countryId)

        then:
        1 * countryService.getCountryById(countryId)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "Create country"() {
        given:
        Country country = new Country(country: "Test Country")
        Country createdCountry = new Country(countryId: "1234", country: "Test Country")

        when:
        ResponseEntity<Country> response = countryController.createCountry(country)

        then:
        1 * countryService.createCountry(country) >> createdCountry
        response.statusCode == HttpStatus.OK
        response.body == createdCountry
    }

    def "Update country"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Updated Country")

        when:
        ResponseEntity<Country> response = countryController.updateCountry(countryId, country)

        then:
        1 * countryService.updateCountry(country) >> country
        response.statusCode == HttpStatus.OK
        response.body == country
    }

    def "Update non-existing country"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Updated Country")

        when:
        ResponseEntity<Country> response = countryController.updateCountry(countryId, country)

        then:
        1 * countryService.updateCountry(country) >> { throw new CountryNotFoundException("Country not found with ID: $countryId") }
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "Patch country"() {
        given:
        String countryId = "1234"
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Patched Country")
        Country updatedCountry = new Country(countryId: countryId, country: "Patched Country")

        when:
        ResponseEntity<Country> response = countryController.patchCountry(countryId, updates)

        then:
        1 * countryService.getCountryById(countryId) >> updatedCountry
        1 * countryService.patchCountry(countryId, updates)
        response.statusCode == HttpStatus.OK
        response.body == updatedCountry
    }

    def "Patch non-existing country"() {
        given:
        String countryId = "1234"
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Patched Country")

        when:
        ResponseEntity<Country> response = countryController.patchCountry(countryId, updates)

        then:
        1 * countryService.getCountryById(countryId) >> { throw new CountryNotFoundException("Country not found with ID: $countryId") }
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "Delete country"() {
        given:
        String countryId = "1234"

        when:
        ResponseEntity<String> response = countryController.deleteCountry(countryId)

        then:
        1 * countryService.deleteCountry(countryId)
        response.statusCode == HttpStatus.OK
        response.body == "Country deleted successfully!"
    }

    def "Get cities by country ID"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Test Country")
        List<City> cities = [new City(), new City()]
        country.setCities(cities)

        when:
        ResponseEntity<List<City>> response = countryController.getCitiesById(countryId)

        then:
        1 * countryService.getCountryById(countryId) >> country
        response.statusCode == HttpStatus.OK
        response.body == cities
    }

    def "Get cities by country ID - Country not found"() {
        given:
        String countryId = "1234"

        when:
        ResponseEntity<List<City>> response = countryController.getCitiesById(countryId)

        then:
        1 * countryService.getCountryById(countryId) >> null
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "Get cities by country ID - No cities found"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Test Country")
        country.setCities([])

        when:
        ResponseEntity<List<City>> response = countryController.getCitiesById(countryId)

        then:
        1 * countryService.getCountryById(countryId) >> country
        response.statusCode == HttpStatus.NO_CONTENT
    }
}

