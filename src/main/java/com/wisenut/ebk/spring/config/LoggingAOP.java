package com.wisenut.ebk.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class LoggingAOP {

    @Before("within(com.wisenut.ebk.spring.service.*)")
//    @Before( "" )
    public void logBefore( JoinPoint joinPoint ) {
        log.debug("Before: " + joinPoint.getSignature().getName());
    }

}
