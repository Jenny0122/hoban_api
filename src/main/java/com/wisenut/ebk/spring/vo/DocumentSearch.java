package com.wisenut.ebk.spring.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 문서 통합 검색 필드 정보
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
@Schema(description = "문서 통합 검색 필드 정보")
public class DocumentSearch {
	
	@Builder.Default String Collection = "documentinfo";
	
	@Builder.Default int TotalCount = 0;
    
	@Builder.Default int Count = 0;
    
	@Builder.Default List<DocumentSearchVo> Result = new ArrayList<>();
}
