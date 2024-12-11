package com.wisenut.ebk.spring.dto;

import com.wisenut.ebk.spring.vo.GroupVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GroupNameDTO {

	int count;

	List<GroupVo> data;
}
