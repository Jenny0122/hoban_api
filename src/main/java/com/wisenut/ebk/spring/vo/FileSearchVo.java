package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
public class FileSearchVo {

	@Schema(description = "파일 OID")
	private String oid;

	@Schema(description = "파일 target OID")
	private String targetoid;

	@Schema(description = "스토리지 파일 ID")
	private String storagefileid;

	@Schema(description = "파일 이름")
	private String filename;

	@Schema(description = "문서 이름")
	private String documentname;

	@Schema(description = "문서 태그")
	private String taglist;

	@Schema(description = "등록자 OID")
	private String creatoroid;

	@Schema(description = "등록자 이름")
	private String creatorname;

	@Schema(description = "등록자 부서 이름")
	private String creatorgroupname;

	@Schema(description = "최종 수정자")
	private String lastmodifieroid;

	@Schema(description = "최종 수정일")
	private String lastmodifiedat;

	@Schema(description = "파일 타입")
	private String filetype;

	@Schema(description = "파일 크기")
	private String filesize;

	@Schema(description = "파일 크기")
	private String filesizem;

	@Schema(description = "폴더 OID")
	private String folderoid;

	@Schema(description = "폴더 Path 이름")
	private String folderfullpathname;

	@Schema(description = "폴더 Path OID")
	private String folderfullpathoid;

	@Schema(description = "관리부서 OID")
	private String managergroupoid;

	@Schema(description = "관리부서 Path OID")
	private String managergroupfullpathoid;

	@Schema(description = "문서 타입 OID")
	private String doctypeoid;

	@Schema(description = "체크아웃 여부")
	private String checkout;

	@Schema(description = "파일 내용")
	private String content;

	@Schema(description = "보안 정보")
	private String aclkeycode;

	@Schema(description = "개인 정보")
	private String customcategory;
	
	@Schema(description = "개인 정보")
	private List<SecurityVo> security;
}
