package com.wisenut.ebk.spring.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchPersonalDTO {
	
	String query;	

	List<Object> data;
	
	Map<String, Integer> customCategoryMap;	
}
