package com.springboot.seckill.dao;

import com.springboot.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,sg.seckill_price,sg.end_date,sg.start_date,sg.stock_count from seckill_goods sg left join goods g on g.id = sg.goods_id")
    public List<GoodsVo> getGoodsVoList();
}
