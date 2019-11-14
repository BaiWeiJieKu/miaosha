package com.imooc.miaosha.redis;

/**
 * Redis存储数据的前缀接口
 *
 * @author Administrator
 */
public interface KeyPrefix {

    /**
     * 获取数据过期时间
     *
     * @return
     */
    int expireSeconds();

    /**
     * 获取前缀
     *
     * @return
     */
    String getPrefix();

}
