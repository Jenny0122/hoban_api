package com.wisenut.ebk.spring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConfigCheck {

    private final ApplicationContext context;

    @Value("${engine.server.ip}")
    String server_ip;

    final int SERVER_PORT = 7000;

    final int TIMEOUT = 10 * 1000;

    @PostConstruct
    public void checkConnectionEngine() {

        QueryAPI530.Search search = new QueryAPI530.Search();
        int ret = 0;

        StringBuilder logBuilder = new StringBuilder();
        log.info("**********************************");
        logBuilder.append("  SearchEngine Connection Test..." + "\n");
        logBuilder.append("\tip: " + server_ip + "\n");
        logBuilder.append("\tport: " + SERVER_PORT + "\n");
        logBuilder.append("\ttimeout: " + TIMEOUT + "\n");
        log.info(logBuilder.toString());
        log.info("**********************************");

        ret = search.w3ConnectServer(server_ip, 7000, 10 * 1000);

        if (ret != 0) {
            log.error("연결 실패");
//            System.exit( SpringApplication.exit( context , ( ) -> 0 ) );
        }
    }

    @Bean
    public ConfigurableServletWebServerFactory configurableServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                super.postProcessContext(context);
                JspPropertyGroup jspPropertyGroup = new JspPropertyGroup();
                jspPropertyGroup.addUrlPattern("*.jsp");
                jspPropertyGroup.setPageEncoding("UTF-8");
                jspPropertyGroup.setScriptingInvalid("false");
                jspPropertyGroup.addIncludePrelude("/WEB-INF/jsp/common/common.jsp");
                jspPropertyGroup.setTrimWhitespace("true");
                jspPropertyGroup.setDefaultContentType("text/html");
                JspPropertyGroupDescriptorImpl jspPropertyGroupDescriptor = new JspPropertyGroupDescriptorImpl(jspPropertyGroup);
                context.setJspConfigDescriptor(new JspConfigDescriptorImpl(Collections.singletonList(jspPropertyGroupDescriptor), Collections.emptyList()));
            }
        };
    }

}
