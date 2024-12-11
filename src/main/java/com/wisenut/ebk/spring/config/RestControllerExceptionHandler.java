package com.wisenut.ebk.spring.config;

import com.wisenut.ebk.spring.MissingArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler( value = MissingArgumentException.class )
    public ResponseEntity< ? > invokeError( MissingArgumentException mae ) {

        final String ERROR_MESSAGE = mae.toString( );

        log.error( "[Error Message]: {}" , ERROR_MESSAGE );

        return ResponseEntity.badRequest( )
                             .body( ERROR_MESSAGE );
    }
}
