package com.example.direction.controller

import com.example.direction.dto.InputDto
import com.example.direction.dto.OutputDto
import com.example.pharmacy.service.PharmacyRecommendationService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

class FormControllerTest extends Specification {

    private MockMvc mockMvc
    private PharmacyRecommendationService pharmacyRecommendationService = Mock()
    private List<OutputDto> outputDtoList


    def setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(new FormController(pharmacyRecommendationService)).build()

        outputDtoList = new ArrayList<>();
        outputDtoList.addAll(
                OutputDto.builder().pharmacyName("pharmacy1").build(),
                OutputDto.builder().pharmacyName("pharmacy2").build(),
        )
    }

    def "GET /"(){
        expect:
        mockMvc.perform(get("/"))
            .andExpect(handler().handlerType(FormController.class))
            .andExpect(handler().methodName("main"))
            .andExpect(status().isOk())
            .andExpect(view().name("main"))
            .andDo(log())
    }

    def "POST /search"() {

        given:
        String inputAddress = "서울 성북구 종암동"

        when:
        /**
         * 왜 content() 가 아닌 param() 메소드가 사용되었는가??
         * html 에서 form post 방식으로 전송되는 content-type 의 경우 application/x-www-form-urlencoded 이다.
         * application/x-www-form-urlencoded 의 경우 key1=value1&key2=value2 의 형식으로 RequestBody 에 담기게 된다.
         * application/json 형태여야만 content() 메소드를 사용할 수 있다.
         * content() doc : {@code application/x-www-form-urlencoded}, the content will be parsed and used to populate the {@link #param(String, String...) request
         * param() doc : In the Servlet API, a request parameter may be parsed from the query string and/or from the body of an application/x-www-form-urlencoded request
         */
        def resultActions = mockMvc.perform(post("/search")
                .param("address",inputAddress))

        then:
        1 * pharmacyRecommendationService.recommendPharmacyList(argument -> {
            assert argument == inputAddress // mock 객체의 argument 검증
        }) >> outputDtoList

        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("output"))
                .andExpect(model().attributeExists("outputFormList")) // model에 outputFormList라는 key가 존재하는지 확인
                .andExpect(model().attribute("outputFormList", outputDtoList))
                .andDo(print())
    }
}
