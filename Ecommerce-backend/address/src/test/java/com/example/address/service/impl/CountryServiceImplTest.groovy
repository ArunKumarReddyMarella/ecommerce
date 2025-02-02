package com.example.address.service.impl


import com.example.address.entity.Country
import com.example.address.exception.CountryAlreadyExistsException
import com.example.address.repository.CountryRepository
import com.example.address.service.impl.CountryServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class CountryServiceImplSpec extends Specification {

    CountryRepository countryRepository = Mock(CountryRepository.class)

    CountryServiceImpl countryService

    def setup() {
        countryService = new CountryServiceImpl(countryRepository: countryRepository)
    }

    def "Get all countries"() {
        given:
        Pageable pageable = Mock(Pageable.class)
        List<Country> countries = [
                new Country(countryId: "1", country: "USA"),
                new Country(countryId: "2", country: "Canada")
        ]
        Page<Country> expectedPage = new PageImpl<>(countries, pageable, countries.size())

        when:
        countryRepository.findAll(pageable) >> { expectedPage }
        Page<Country> actualPage = countryService.getCountries(pageable)

        then:
        actualPage == expectedPage
        1 * countryRepository.findAll(pageable)
    }

    def "Get country by ID - Success"() {
        given:
        String countryId = "1"
        Country country = new Country(countryId: countryId, country: "USA")

        when:
        countryRepository.findById(countryId) >> { Optional.of(country) }
        Country foundCountry = countryService.getCountryById(countryId)

        then:
        foundCountry == country
        1 * countryRepository.findById(countryId)
    }

    def "Get country by ID - Not Found"() {
        given:
        String countryId = "1"

        when:
        countryRepository.findById(countryId) >> { Optional.empty() }
        countryService.getCountryById(countryId)

        then:
        thrown CountryNotFoundException
        1 * countryRepository.findById(countryId)
    }

    def "create country with null id should generate a new id"() {
        given:
        Country country = new Country(country: "Test Country")

        when:
        countryRepository.saveAndFlush(country) >> { country }
        Country createdCountry = countryService.createCountry(country)

        then:
        createdCountry.countryId != null
        createdCountry.country == "Test Country"
    }

    def "create country with existing id should throw CountryAlreadyExistsException"() {
        given:
        Country existingCountry = new Country(countryId: "existing-id", country: "Existing Country")
        Country country = new Country(countryId: "existing-id", country: "Test Country")

        when:
        countryRepository.findById("existing-id") >> { Optional.of(existingCountry) }
        countryService.createCountry(country)

        then:
        thrown CountryAlreadyExistsException
    }

    def "create country with new id should save country to repository"() {
        given:
        Country country = new Country(countryId: "new-id", country: "Test Country")

        when:
        countryRepository.findById("new-id") >> {Optional.empty()}
        countryRepository.saveAndFlush(country) >> { country }
        Country createdCountry = countryService.createCountry(country)

        then:
        createdCountry.countryId == "new-id"
        createdCountry.country == "Test Country"
    }

    def "Update country - Success"() {
        given:
        Country countryToUpdate = new Country(countryId: "1", country: "USA")

        when:
        countryRepository.existsById(countryToUpdate.getCountryId()) >> { true }
        countryRepository.saveAndFlush(countryToUpdate) >> { countryToUpdate }
        Country updatedCountry = countryService.updateCountry(countryToUpdate)

        then:
        updatedCountry == countryToUpdate
        1 * countryRepository.existsById(countryToUpdate.getCountryId())
        1 * countryRepository.saveAndFlush(countryToUpdate)
    }

    def "Update country - Not Found"() {
        given:
        Country countryToUpdate = new Country(countryId: "1", country: "USA")

        when:
        countryRepository.existsById(countryToUpdate.getCountryId()) >> { false }
        countryService.updateCountry(countryToUpdate)

        then:
        thrown CountryNotFoundException
        1 * countryRepository.existsById(countryToUpdate.getCountryId())
        0 * countryRepository.saveAndFlush(countryToUpdate)
    }

    def "Patch country - Success"() {
        given:
        String countryId = "1"
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Canada")
        Country existingCountry = new Country(countryId: countryId, country: "USA")
        Country updatedCountry = new Country(countryId: countryId, country: "Canada")

        when:
        countryRepository.findById(countryId) >> { Optional.of(existingCountry) }
        countryRepository.save(existingCountry) >> { updatedCountry }
        countryService.patchCountry(countryId, updates)

        then:
        1 * countryRepository.findById(countryId)
        1 * countryRepository.save(existingCountry)
        existingCountry.country == "Canada"
    }

    def "Patch country - Not Found"() {
        given:
        String countryId = "1"
        Map<String, Object> updates = new HashMap<>()
        updates.put("country", "Canada")

        when:
        countryRepository.findById(countryId) >> { Optional.empty() }
        countryService.patchCountry(countryId, updates)

        then:
        thrown(CountryNotFoundException)
        1 * countryRepository.findById(countryId)
        0 * countryRepository.save(any())
    }

    def "Delete country - Success"() {
        given:
        String countryId = "1"

        when:
        countryRepository.existsById(countryId) >> { true }
        countryService.deleteCountry(countryId)

        then:
        1 * countryRepository.existsById(countryId)
        1 * countryRepository.deleteById(countryId)
    }

    def "Delete country - Not Found"() {
        given:
        String countryId = "1"

        when:
        countryRepository.existsById(countryId) >> { false }
        countryService.deleteCountry(countryId)

        then:
        thrown CountryNotFoundException
        1 * countryRepository.existsById(countryId)
        0 * countryRepository.deleteById(countryId)
    }
}
