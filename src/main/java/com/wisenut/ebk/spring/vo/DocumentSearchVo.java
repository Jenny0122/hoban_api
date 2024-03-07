package com.wisenut.ebk.spring.vo;

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
public class DocumentSearchVo {
	
	@Schema(description = "문서 이름")
	private String name;
	
	@Schema(description = "상세 설명")
	private String description;
	
	@Schema(description = "문서 태그")
	private String taglist;	
	
	@Schema(description = "등록자 OID")
	private String creatoroid;

	@Schema(description = "등록자 이름")
	private String creatorname;
	
	@Schema(description = "등록자 부서 이름")
	private String creatorgroupname;

	@Schema(description = "협엽 관련")
	private String director;
	
	@Schema(description = "협엽 관련")
	private String author;
	
	@Schema(description = "협엽 관련")
	private String coauthor;
	
	@Schema(description = "협엽 관련")
	private String reviewer;
	
	@Schema(description = "협엽 관련")
	private String approver;
	
	@Schema(description = "협엽 관련")
	private String receiver;
	
	@Schema(description = "등록일")
	private String createdat;	
	
	@Schema(description = "수정일")
	private String lastmodifiedat;
	
	@Schema(description = "폴더 Path 이름")
	private String folderfullpathname;
	
	@Schema(description = "문서 타입 OID")
	private String doctypeoid;
	
	@Schema(description = "보안 정보")
	private String aclkeycode;
	
	//@Schema(description = "폴더 OID")
	//private String folderoid;
	
	//@Schema(description = "폴더 Path OID")
	//private String folderfullpathoid;

	//@Schema(description = "관리부서 OID")
	//private String managergroupoid;
	
	//@Schema(description = "관리부서 Path OID")
	//private String managergroupfullpathoid;
	
	//@Schema(description = "체크아웃 여부")
	//private String checkout;

	}
