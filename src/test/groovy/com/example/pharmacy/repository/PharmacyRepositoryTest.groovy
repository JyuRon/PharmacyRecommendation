package com.example.pharmacy.repository

import com.example.AbstractIntegrationContainerBaseTest
import com.example.pharmacy.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    // 각 테스트 메서드가 실행될기 전에 실행됨
    def setup(){
        pharmacyRepository.deleteAll()
    }

    def "PharmacyRepository save"(){
        given:
        String address = "서울특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11
        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        def result = pharmacyRepository.save(pharmacy)

        then:
        result.getPharmacyAddress() == address
        result.getPharmacyName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude

    }

    def "PharmacyRepository saveAll"(){
        given:
        String address = "서울특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 36.11
        double longitude = 128.11
        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        pharmacyRepository.saveAll(List.of(pharmacy))
        def result = pharmacyRepository.findAll()

        then:
        result.size() == 1



    }
}
