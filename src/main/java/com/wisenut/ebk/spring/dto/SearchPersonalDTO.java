package com.wisenut.ebk.spring.dto;

import com.wisenut.ebk.spring.vo.GroupVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchPersonalDTO {
	
	//String query;

	@Builder.Default List<Object> data = Collections.emptyList();

	@Builder.Default List<GroupVo> groups = Collections.emptyList();

	@Builder.Default Map<String, Integer> customCategoryMap = Collections.EMPTY_MAP;
}
