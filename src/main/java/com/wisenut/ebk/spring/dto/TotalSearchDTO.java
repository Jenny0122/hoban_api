package com.wisenut.ebk.spring.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TotalSearchDTO {
	
	String query;	

	List<Object> data;
	
}
