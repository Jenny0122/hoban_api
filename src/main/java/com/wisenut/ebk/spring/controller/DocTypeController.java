package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.service.DocTypeService;
import com.wisenut.ebk.spring.vo.DocTypeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DocTypeController {
    private final DocTypeService docTypeService;

    @GetMapping( "/doctypes" )
    public ResponseEntity< List< DocTypeVo > > getDocTypes( ) {
        return ResponseEntity.ok( docTypeService.getDocTypes( ) );
    }
}
