package com.example.direction.service;

import com.example.api.dto.DocumentDto;
import com.example.api.service.KakaoCategorySearchService;
import com.example.direction.entity.Direction;
import com.example.direction.repository.DirectionRepository;
import com.example.pharmacy.dto.PharmacyDto;
import com.example.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    // 약국 최대 검색 갯수
    private static final int MAX_SEARCH_COUNT = 3;

    // 반경 10km
    private static final double RADIUS_KM = 10.0;

    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;
    private final KakaoCategorySearchService kakaoCategorySearchService;

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList){
        if(CollectionUtils.isEmpty(directionList)){
            return Collections.emptyList();
        }

        return directionRepository.saveAll(directionList);
    }

    // 카카오 주소 api -> 공공데이터포털에서 받아 저장된 DB 정보 조회 반환
    public List<Direction> buildDirectionList(DocumentDto documentDto){

        if(Objects.isNull(documentDto)){
            return Collections.emptyList();
        }

        return pharmacySearchService.searchPharmacyDtoList().stream()
                .map(pharmacyDto ->
                    Direction.builder()
                            .inputAddress(documentDto.getAddressName())
                            .inputLatitude(documentDto.getLatitude())
                            .inputLongitude(documentDto.getLongitude())
                            .targetPharmacyName(pharmacyDto.getPharmacyName())
                            .targetAddress(pharmacyDto.getPharmacyAddress())
                            .targetLatitude(pharmacyDto.getLatitude())
                            .targetLongitude(pharmacyDto.getLongitude())
                            .distance(calculateDistance(
                                    documentDto.getLatitude(), documentDto.getLongitude(),
                                    pharmacyDto.getLatitude(), pharmacyDto.getLongitude()
                            ))
                            .build()
                )
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());


    }

    // 카카오 주소 api -> 카카오 카테고리 api
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto){
        if(Objects.isNull(inputDocumentDto)){
            return Collections.emptyList();
        }

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
                .getDocumentList()
                .stream()
                .map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetPharmacyName(resultDocumentDto.getPlaceName())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                .distance(resultDocumentDto.getDistance() * 0.001) // km 단위
                                .build()
                        )
                .limit(3)
                .collect(Collectors.toList());
    }


    // Haversine formula, 위도 & 경도를 사용하여 두 지점의 거리를 구하는 공식
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
