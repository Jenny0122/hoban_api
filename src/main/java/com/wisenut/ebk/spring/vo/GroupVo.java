package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 문서 통합 검색 필드 정보
 *
 * @author 안선정
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
@Entity(name="vftrgroup")
public class GroupVo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String oid;

    @Schema(description = "부셔명")
    private String name;

    @Schema(description = "부서 코드")
    private String groupcode;

    @Schema(description = "상위 부서 OID")
    private String parentoid;

    @Schema(description = "부서 index")
    private String fullpathindex;
}
