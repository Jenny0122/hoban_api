package com.wisenut.ebk.spring.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema( description = "부서명 필드 정보" )
@Entity( name = "vftrdoctype" )
public class DocTypeVo {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    String oid;

    @Schema( description = "문서 유형" )
    private String name;


}
