package com.example.api.service;

import com.example.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String restApiKey;

    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000)
    )
    public KakaoApiResponseDto requestAddressSearch(String address){

        if(ObjectUtils.isEmpty(address)){
            return null;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION,"KakaoAK "+ restApiKey);

        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

        // kakao api 호출
        ResponseEntity<KakaoApiResponseDto> exchange = restTemplate.exchange(
                kakaoUriBuilderService.buildUriByAddressSearch(address),
                HttpMethod.GET,
                httpEntity,
                KakaoApiResponseDto.class
        );

        return exchange.getBody();
    }

    @Recover
    public KakaoApiResponseDto recover(RuntimeException e, String address){
        log.error("All thr retries failed. address: {}, error: {}", address, e.getMessage());
        return null;
    }
}
