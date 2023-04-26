package com.example.direction.controller

import com.example.direction.service.DirectionService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class DirectionControllerTest extends Specification {

    private MockMvc mockMvc
    private DirectionService directionService = Mock()

    def setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(new DirectionController(directionService))
            .build()

    }

    def "GET /dir/{encodedId}"(){
        given:
        String encodedId = "r"
        String redirectUrl = "https://map.kakao.com/link/map/winter,38.11,128.11"

        when:
        1 * directionService.findDirectionUrlById(argument -> {
            assert argument == encodedId
        }) >> redirectUrl

        then:
        mockMvc.perform(get("/dir/{encodedId}",encodedId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl))
            .andDo(print())
    }


}
