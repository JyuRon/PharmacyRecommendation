package com.example.pharmacy.service

import com.example.AbstractIntegrationContainerBaseTest
import com.example.pharmacy.entity.Pharmacy
import com.example.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup(){
        pharmacyRepository.deleteAll()
    }

    def "PharmacyRepository update - dirty checking success"(){
        given:
        String inputAddress = "서울특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11
        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddress(entity.getId(), modifiedAddress)

        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == modifiedAddress

    }

    def "PharmacyRepository update - dirty checking fail"(){
        given:
        String inputAddress = "서울특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11
        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddressWithoutTransactional(entity.getId(), modifiedAddress)

        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() != modifiedAddress

    }
}
