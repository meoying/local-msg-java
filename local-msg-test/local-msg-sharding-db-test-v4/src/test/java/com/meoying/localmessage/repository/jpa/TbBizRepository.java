package com.meoying.localmessage.repository.jpa;

import com.meoying.localmessage.repository.entity.TbBiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface TbBizRepository extends JpaRepository<TbBiz, Long> {

}