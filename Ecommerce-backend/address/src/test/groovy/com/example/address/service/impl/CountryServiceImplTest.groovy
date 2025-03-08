package com.example.address.service.impl

import com.example.address.entity.Country
import com.example.address.exception.CountryAlreadyExistsException
import com.example.address.exception.CountryNotFoundException
import com.example.address.repository.CountryRepository
import com.example.address.service.impl.CountryServiceImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class CountryServiceImplTest extends Specification {

    private CountryRepository countryRepository = Mock(CountryRepository)
    private CountryServiceImpl countryService = new CountryServiceImpl(countryRepository: countryRepository)

    def "Get all countries"() {
        given:
        Pageable pageable = Pageable.unpaged()
        List<Country> countries = [new Country(), new Country()]
        Page<Country> expectedPage = new PageImpl<>(countries, pageable, countries.size())

        when:
        Page<Country> actualPage = countryService.getCountries(pageable)

        then:
        1 * countryRepository.findAll(pageable) >> expectedPage
        actualPage == expectedPage
    }

    def "Get country by ID"() {
        given:
        String countryId = "1234"
        Country country = new Country(countryId: countryId, country: "Test Country")

        when:
        Country foundCountry = countryService.getCountryById(countryId)

        then:
        1 * countryRepository.findById(countryId) >> Optional.of(country)
        foundCountry == country
    }

    def "Get country by ID not found"() {
        given:
        String countryId = "1234"

        when:
        countryService.getCountryById(countryId)

        then:
        1 * countryRepository.findById(countryId) >> Optional.empty()
        thrown(CountryNotFoundException)
    }

    def "Create new country"() {
        given:
        Country country = new Country(countryId: null, country: "Test Country")

        when:
        Country createdCountry = countryService.createCountry(country)

        then:
        1 * countryRepository.saveAndFlush(country) >> country
        createdCountry.getCountryId() != null
    }

    def "Create country with existing ID"() {
        given:
        Country country = new Country(countryId: "1234", country: "Test Country")
        Country expectedCountry = new Country(countryId: "1234", country: "Test Country")

        when:
        countryService.createCountry(country)

        then:
        1 * countryRepository.findById(country.getCountryId()) >> Optional.empty()
        1 * countryRepository.saveAndFlush(country) >> expectedCountry
        assertEquals(expectedCountry, country)
    }

    def "Create country that already exists"() {
        given:
        Country country = new Country(countryId: "1234", country: "Test Country")

        when:
        countryService.createCountry(country)

        then:
        1 * countryRepository.findById(country.getCountryId()) >> Optional.of(country)
        thrown(CountryAlreadyExistsException)
    }

    def "Update country"() {
        given:
        String countryId = "1234"
        Country existingCountry = new Country(countryId: countryId, country: "Existing Country")
        Country updatedCountry = new Country(countryId: countryId, country: "Updated Country")

        when:
        Country returnedCountry = countryService.updateCountry(updatedCountry)

        then:
        1 * countryRepository.existsById(countryId) >> true
        1 * countryRepository.saveAndFlush(updatedCountry) >> updatedCountry
        returnedCountry == updatedCountry
    }

    def "Update non existing country"() {
        given:
        String countryId = "1234"
        Country updatedCountry = new Country(countryId: countryId, country: "Updated Country")

        when:
        countryService.updateCountry(updatedCountry)

        then:
        1 * countryRepository.existsById(countryId) >> false
        thrown(CountryNotFoundException)
    }

    def "Patch country"() {
        given:
        String countryId = "1234"
        Country existingCountry = new Country(countryId: countryId, country: "Existing Country")
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Patched Country")

        when:
        countryService.patchCountry(countryId, updates)

        then:
        1 * countryRepository.findById(countryId) >> Optional.of(existingCountry)
        1 * countryRepository.save(existingCountry)
        existingCountry.getCountry() == "Patched Country"
    }

    def "Patch invalid field Update country"() {
        given:
        String countryId = "1234"
        Country existingCountry = new Country(countryId: countryId, country: "Existing Country")
        Map<String, Object> updates = new HashMap<>()
        updates.put("invalidField", "Patched Country")

        when:
        countryService.patchCountry(countryId, updates)

        then:
        1 * countryRepository.findById(countryId) >> Optional.of(existingCountry)
        thrown(IllegalArgumentException)
    }

    def "Patch non-existing country"() {
        given:
        String countryId = "1234"
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Patched Country")

        when:
        countryService.patchCountry(countryId, updates)

        then:
        1 * countryRepository.findById(countryId) >> Optional.empty()
        thrown(CountryNotFoundException)
    }

    def "Delete country"() {
        given:
        String countryId = "1234"

        when:
        countryService.deleteCountry(countryId)

        then:
        1 * countryRepository.existsById(countryId) >> true
        1 * countryRepository.deleteById(countryId)
    }

    def "Delete non existing country"() {
        given:
        String countryId = "1234"

        when:
        countryService.deleteCountry(countryId)

        then:
        1 * countryRepository.existsById(countryId) >> false
        thrown(CountryNotFoundException)
    }

    static void assertEquals(Country expectedCountry, Country actualCountry) {
        assert expectedCountry.countryId == actualCountry.countryId
        assert expectedCountry.country == actualCountry.country
    }
}

