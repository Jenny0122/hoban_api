package com.wisenut.ebk.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class IndexController {

    final String path = "/search.do";

//    @GetMapping( path )
//    public String index( ) {
//
//        log.info( "{} 실행" , path );
//
//        return "search";
//    }
//
//    @GetMapping( "/" )
//    public String indexRedirect( ) {
//        log.info("intro");
//        return "intro";
//    }
}