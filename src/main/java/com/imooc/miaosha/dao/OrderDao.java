package com.imooc.miaosha.dao;

import org.apache.ibatis.annotations.*;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;

@Mapper
public interface OrderDao {

    /**
     * 通过用户id和商品id查询该用户是否已经秒杀过了
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /**
     * 插入订单详情
     * 并且返回插入的订单id
     *
     * @param orderInfo
     * @return
     */
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 添加秒杀订单
     *
     * @param miaoshaOrder
     * @return
     */
    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);


    /**
     * 根据订单号查询订单详情
     *
     * @param orderId
     * @return
     */
    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);

    @Delete("delete from order_info")
    void deleteOrders();

    @Delete("delete from miaosha_order")
    void deleteMiaoshaOrders();
}
