package com.imooc.miaosha.redis;

/**
 * 订单缓存键
 *
 * @author Administrator
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");

}
