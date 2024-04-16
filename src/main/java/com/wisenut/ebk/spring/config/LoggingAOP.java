package com.wisenut.ebk.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Configuration
public class LoggingAOP {

    @Before( "within(com.wisenut.ebk.spring.controller.*)" )
    public void logBefore( JoinPoint joinPoint ) {

        log.info( "**********************************" );
        log.info( "  Before: {}\n" , joinPoint.getSignature( )
                                               .getName( ) );

        for ( Object o : joinPoint.getArgs( ) ) {
            if ( o instanceof Map ) {
                @SuppressWarnings( "unchecked" ) Map< String, String > params = ( HashMap< String, String > ) o;
                params.forEach( ( key , value ) -> log.info( "    - {}: {}" , key , value ) );
            }
        }

        log.info("");

        for ( Object o : joinPoint.getArgs( ) ) {
            if ( o instanceof HttpServletRequest ) {
                @SuppressWarnings( "unchecked" ) HttpServletRequest request = ( HttpServletRequest ) o;
                String ip = request.getHeader( "X-Forwarded-For" );
                if ( ip == null ) ip = request.getRemoteAddr( );

                log.info( "    - IP: {}" , ip );
            }
        }

        log.info( "**********************************" );

    }
}