package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.service.ExcelService;
import com.wisenut.ebk.spring.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ExcelController {

    private final ExcelService service;
    private final SearchService searchService;
    @Value( "${key.sensitive}" )
    String sensitiveKey;
    @Value( "${key.personal}" )
    String personalKey;
    @Value( "${limit.excel.rows}" )
    int rowsLimit;

    @Deprecated
    @PostMapping( "/personal/excel" )
    public void getPersonalDataExcelOld( HttpServletRequest request , HttpServletResponse res ) throws Exception {

        HttpSession session = request.getSession( );
        SearchPersonalDTO dto = ( SearchPersonalDTO ) session.getAttribute( personalKey );
        service.getPersonalDataExcel( res , dto );
    }

    @Deprecated
    @PostMapping( "/sensitive/excel" )
    public void getSensitiveDataExcelOld( HttpServletRequest request , HttpServletResponse res ) throws Exception {
        HttpSession session = request.getSession( );
        TotalSearchDTO dto = ( TotalSearchDTO ) session.getAttribute( sensitiveKey );
        service.getSensitiveDataExcel( res , dto );
    }

    @GetMapping( "/personal/excel" )
    public void getPersonalDataExcel( HttpServletRequest request , HttpServletResponse res ) throws Exception {

        HttpSession session = request.getSession( );

        Map< String, String > params = ( Map< String, String > ) session.getAttribute( personalKey );

        params.put( "count" , String.valueOf( rowsLimit ) );
        params.put( "pageStart" , "0" );
        SearchPersonalDTO dto = searchService.searchPersonalDataTotalListByCategory( params );

        service.getPersonalDataExcel( res , dto );
    }

    @GetMapping( "/sensitive/excel" )
    public void getSensitiveDataExcel( HttpServletRequest request , HttpServletResponse res ) throws Exception {
        HttpSession session = request.getSession( );

        Map< String, String > params = ( Map< String, String > ) session.getAttribute( sensitiveKey );

        String query = params.getOrDefault( "query" , "" );

        params.put( "count" , String.valueOf( rowsLimit ) );
        params.put( "pageStart" , "0" );

        List< Object > temp = new ArrayList<>( );
        temp.add( searchService.searchSensitiveFileTotalListByCategory( params ) );
        TotalSearchDTO dto = TotalSearchDTO.builder( )
                                           .data( temp )
                                           .query( query )
                                           .build( );

        service.getSensitiveDataExcel( res , dto );
    }
}
