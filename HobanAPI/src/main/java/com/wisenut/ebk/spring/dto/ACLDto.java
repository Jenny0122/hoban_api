package com.wisenut.ebk.spring.dto;

import lombok.Data;

@Data
public class ACLDto {
	
	String success;
	
	String errorCode;
	
	String errorMessage;
	
	ACLData data;
}
