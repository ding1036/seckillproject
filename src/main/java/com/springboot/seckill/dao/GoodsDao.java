package com.springboot.seckill.dao;

import com.springboot.seckill.domain.Goods;
import com.springboot.seckill.domain.SecKillGoods;
import com.springboot.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,sg.seckill_price,sg.end_date,sg.start_date,sg.stock_count from seckill_goods sg left join goods g on g.id = sg.goods_id")
    public List<GoodsVo> getGoodsVoList();
    @Select("select g.*,sg.seckill_price,sg.end_date,sg.start_date,sg.stock_count from seckill_goods sg left join goods g on g.id = sg.goods_id where g.id = #{goodsId}")
    public GoodsVo getGoodsVo(@Param("goodsId")long goodsId);
    @Update("update seckill_goods set stock_count = stock_count -1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock( SecKillGoods secKillGoods);
}
