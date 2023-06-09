package com.example.pharmacy.cache;

import com.example.pharmacy.dto.PharmacyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyRedisTemplateService {

    private static final String CACHE_KEY = "PHARMACY";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    // 생성자가 생성된 이후
    @PostConstruct
    public void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(PharmacyDto pharmacyDto){
        if(Objects.isNull(pharmacyDto) || Objects.isNull(pharmacyDto.getId())){
            log.info("Required Values must not be null");
            return;
        }

        try{
            hashOperations.put(CACHE_KEY, pharmacyDto.getId().toString(), serializePharmacyDto(pharmacyDto));
            log.error("[PharmacyRedisTemplateService save success] id: {}", pharmacyDto.getId());
        }catch (Exception e){
            log.error("[PharmacyRedisTemplateService save error] {}", e.getMessage());
        }
    }

    public List<PharmacyDto> findAll(){
        try{
            List<PharmacyDto> list = new ArrayList<>();
            for(String value : hashOperations.entries(CACHE_KEY).values()){
                PharmacyDto pharmacyDto = deSerializePharmacyDto(value);
                list.add(pharmacyDto);
            }
            log.info("[PharmacyRedisTemplateService findAll] Success");
            return list;
        }catch (Exception e){
            log.error("[PharmacyRedisTemplateService findAll error] {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(Long id){
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[PharmacyRedisTemplateService delete]: {}", id);
    }

    private String serializePharmacyDto(PharmacyDto pharmacyDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pharmacyDto);
    }

    private PharmacyDto deSerializePharmacyDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, PharmacyDto.class);
    }

}
