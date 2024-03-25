package com.wisenut.ebk.spring.service;

import com.wisenut.ebk.spring.repository.DocTypeRepository;
import com.wisenut.ebk.spring.vo.DocTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocTypeService {

    @Autowired
    private DocTypeRepository docTypeRepository;

    public List< DocTypeVo > getDocTypes( ) {

        List< DocTypeVo > dataList = docTypeRepository.findAll( );
//        dataList.forEach(v -> log.info(v.toString()));
//        System.out.println(dataList.size());

//        return databaseConnector.getDataFromDB();

        return dataList;
    }
}
