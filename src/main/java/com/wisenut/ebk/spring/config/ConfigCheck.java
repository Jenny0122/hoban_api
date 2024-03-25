package com.wisenut.ebk.spring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConfigCheck {

    private final ApplicationContext context;
    @Value( "${engine.server.ip}" )
    String new_ip;

    @PostConstruct
    public void checkConnectionEngine( ) {

        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        ret = search.w3ConnectServer( new_ip , 7000 , 10 * 1000 );

        if ( ret != 0 ) {
            log.error( "연결 실패" );
//            System.exit( SpringApplication.exit( context , ( ) -> 0 ) );
        }
    }
}
