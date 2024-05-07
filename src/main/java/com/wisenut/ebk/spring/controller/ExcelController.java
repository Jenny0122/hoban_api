package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ExcelController {

    private final ExcelService service;
    
    @Value( "${key.sensitive}" )
    String sensitiveKey;
    @Value( "${key.personal}" )
    String personalKey;

    @GetMapping( "/personal/excel" )
    public void getPersonalDataExcel( HttpServletRequest request , HttpServletResponse res ) throws Exception {

        HttpSession session = request.getSession( );
        SearchPersonalDTO dto = (SearchPersonalDTO)session.getAttribute( personalKey );
        service.getPersonalDataExcel( res , dto);
    }

    @GetMapping( "/sensitive/excel" )
    public void getSensitiveDataExcel( HttpServletRequest request , HttpServletResponse res ) throws Exception {
        HttpSession session = request.getSession( );
        TotalSearchDTO dto = ( TotalSearchDTO ) session.getAttribute( sensitiveKey );
        service.getSensitiveDataExcel( res , dto );
    }
}
