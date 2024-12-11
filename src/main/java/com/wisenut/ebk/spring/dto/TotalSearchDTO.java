package com.wisenut.ebk.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TotalSearchDTO {
	
	String query;	

	List<Object> data;
	
}
