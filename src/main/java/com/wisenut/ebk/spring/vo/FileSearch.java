package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Collections;
import java.util.List;

/**
 * 파일 통합 검색 필드 정보
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
@Schema(description = "파일 통합 검색 필드 정보")
public class FileSearch {

	@Builder.Default String Collection = "fileinfo";
	
	@Builder.Default int TotalCount = 0;
    
	@Builder.Default int Count = 0;
    
	@Builder.Default List<FileSearchVo> Result = Collections.emptyList()
			;
	@Builder.Default List<GroupVo> groups = Collections.emptyList();
}
