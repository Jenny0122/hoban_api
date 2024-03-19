package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.service.GroupNameService;
import com.wisenut.ebk.spring.vo.GroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupNameController {

    @Autowired
    private GroupNameService groupNameService;

    @PostMapping("/groups")
    public ResponseEntity<List<GroupVo>> getGroupNames() {
        List<GroupVo> groupNames = groupNameService.getGroupNames();
        return ResponseEntity.ok(groupNames);
    }
}