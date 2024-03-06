package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class ExcelController {

    private final ExcelService service;


    @GetMapping("/personal/excel")
    public void getPersonalDataExcel(HttpServletResponse res) throws Exception {
        service.getPersonalDataExcel(res);
    }

    @GetMapping("/sensitive/excel")
    public void getSensitiveDataExcel(HttpServletResponse res) throws Exception {
        service.getSensitiveDataExcel(res);
    }
}
