package com.imooc.miaosha.service;

import java.util.Date;

import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 通过用户id和商品id查询该用户是否已经秒杀过了
     *
     * @param userId
     * @param goodsId
     * @return
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        //查询缓存
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
    }

    /**
     * 创建订单，返回订单详情
     *
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        //添加订单详情
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);//订单创建未支付，应该使用枚举来表示
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);


        //添加秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        long insert = orderDao.insertMiaoshaOrder(miaoshaOrder);
        if (insert <= 0) {
            throw new GlobalException(CodeMsg.REPEATE_MIAOSHA);
        }

        //写入缓存
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }


    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public void deleteOrders() {
            orderDao.deleteOrders();
            orderDao.deleteMiaoshaOrders();
    }
}
