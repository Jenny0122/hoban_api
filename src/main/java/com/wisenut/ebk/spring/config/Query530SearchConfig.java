package com.wisenut.ebk.spring.config;


import QueryAPI530.Search;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class Query530SearchConfig {

    @Value( "${engine.server.ip}" )
    String server_ip;
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Bean
    public Search getSearch() {
        int ret = 0;
        Search search = new Search();

        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3ConnectServer( server_ip , 7000 , 10 * 1000 );

        return search;
    }
}
