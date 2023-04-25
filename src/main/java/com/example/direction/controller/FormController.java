package com.example.direction.controller;

import com.example.direction.dto.InputDto;
import com.example.pharmacy.service.PharmacyRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FormController {

    private final PharmacyRecommendationService pharmacyRecommendationService;

    @GetMapping("/")
    public String main(){
        return "main";
    }

    @PostMapping("/search")
    public String postDirection(InputDto inputDto, Model model){
        model.addAttribute("outputFormList",
                pharmacyRecommendationService.recommendPharmacyList(inputDto.getAddress())
        );
        return "output";
    }
}
