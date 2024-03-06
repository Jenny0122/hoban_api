package com.wisenut.ebk.spring;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.wisenut.ebk.spring.dto.ACLDto;

@SpringBootTest
class HobanApplicationTests {

	@Test
	void contextLoads() throws Exception {

		URI uri = UriComponentsBuilder.newInstance()
						.scheme("https")
						.host("ecmdev.e-hoban.co.kr")
						.path("/api/getACLInfos")
						.queryParam("OID", "")
						.build()
						.toUri();
		
		ResponseEntity<ACLDto> response = new TestRestTemplate().postForEntity(uri, null, ACLDto.class);
		ACLDto responseBody = response.getBody();
		String aclInfo;
		if(responseBody.getErrorCode().contentEquals("NoUserCache")) {
			throw new Exception(responseBody.getErrorMessage());
		} else {
			aclInfo = responseBody.getData().getACLInfos();
			System.out.println("aclInfo: " + aclInfo);
		}

		
	}

}
