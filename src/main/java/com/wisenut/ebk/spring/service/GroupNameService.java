package com.wisenut.ebk.spring.service;

import com.wisenut.ebk.spring.repository.GroupNameRepository;
import com.wisenut.ebk.spring.vo.GroupVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupNameService {

    //@Autowired
    //private DatabaseConnector databaseConnector;
    @Autowired
    private GroupNameRepository groupNameRepository;

    public List<GroupVo> getGroupNames() {

        List<GroupVo> dataList = groupNameRepository.findAll();
//        dataList.forEach(v -> log.info(v.toString()));
//        System.out.println(dataList.size());

//        return databaseConnector.getDataFromDB();

        return dataList;
    }
}
