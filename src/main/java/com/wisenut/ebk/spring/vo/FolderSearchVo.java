package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 폴더 통합 검색 필드 정보
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
@Schema(description = " 폴더 통합 검색 필드 정보")
public class FolderSearchVo {
		
	@Schema(description = "폴더 OID")
	private String oid;
	
	@Schema(description = "폴더 이름")
	private String foldername;
	
	@Schema(description = "설명")
	private String description;
	
	@Schema(description = "등록자 OID")
	private String creatoroid;
	
	@Schema(description = "등록자 부서 이름")
	private String creatorgroupname;
	
	@Schema(description = "등록일")
	private String createdat;
	
	@Schema(description = "최종수정일")
	private String lastmodifiedat;
	
	@Schema(description = "폴더 Path 이름")
	private String folderfullpathname;
	
	@Schema(description = "폴더 Path OID")
	private String folderfullpathoid;
	
	@Schema(description = "관리부서 OID")
	private String managergroupoid;
	
	@Schema(description = "관리부서 Path OID")
	private String managergroupfullpathoid;
	
	@Schema(description = "폴더 Path")
	private String fullpathindex;
	
	@Schema(description = "지식 폴더")
	private String knowledgefolderlist;
	
	@Schema(description = "폴더 유형")
	private String doctypefolder;	
	
	@Schema(description = "보안 정보")
	private String aclkeycode;
	
}
