package com.wisenut.ebk.spring.repository;


import com.wisenut.ebk.spring.vo.GroupVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupNameRepository extends JpaRepository<GroupVo, String> {
//    List<GroupNameVo> getAll();
}
