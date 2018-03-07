package com.springboot.seckill.dao;

import com.springboot.seckill.domain.SecKillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SecKillUserDao {
	
	@Select("select * from seckill_user where id = #{id}")
	public SecKillUser getById(@Param("id")long id);
}
