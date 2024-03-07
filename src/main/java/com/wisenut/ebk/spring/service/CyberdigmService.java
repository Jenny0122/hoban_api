package com.wisenut.ebk.spring.service;


import com.wisenut.ebk.spring.dto.ACLDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@Service
@Slf4j
public class CyberdigmService {

    RestTemplate restTemplate = new RestTemplate();

    public String getAclInfo() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                                      .scheme("https")
                                      .host("ecmdev.e-hoban.co.kr")
                                      .path("/api/getACLInfos")
                                      .queryParam("OID", "")
                                      .build()
                                      .toUri();

        ResponseEntity<ACLDto> response = restTemplate.postForEntity(uri, null, ACLDto.class);
        ACLDto responseBody = response.getBody();
        String aclInfo;
        if (responseBody.getErrorCode()
                        .contentEquals("NoUserCache")) {
            throw new Exception(responseBody.getErrorMessage());
        } else {
            aclInfo = responseBody.getData()
                                  .getACLInfos();
        }

        return aclInfo;

    }
}