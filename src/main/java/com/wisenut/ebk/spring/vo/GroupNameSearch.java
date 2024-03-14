package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 부서명 필드 정보
 * 
 * @author 안선정
 * 
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "부서명 필드 정보")
public class GroupNameSearch {
	
	@Builder.Default String Collection = "groupinfo";
	
	@Builder.Default int TotalCount = 0;
    
	@Builder.Default int Count = 0;
    
	@Builder.Default List<GroupVo> Result = new ArrayList<>();
}
