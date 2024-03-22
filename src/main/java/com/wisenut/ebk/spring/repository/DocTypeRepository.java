package com.wisenut.ebk.spring.repository;


import com.wisenut.ebk.spring.vo.DocTypeVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeRepository extends JpaRepository< DocTypeVo, String > {
}
