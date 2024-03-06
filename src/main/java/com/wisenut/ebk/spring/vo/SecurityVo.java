package com.wisenut.ebk.spring.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class SecurityVo {
	
	String securityInfo;
	
	String securityCount;

}
