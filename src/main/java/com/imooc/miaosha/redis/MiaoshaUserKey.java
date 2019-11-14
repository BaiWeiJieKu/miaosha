package com.imooc.miaosha.redis;

/**
 * 秒杀用户键
 *
 * @author Administrator
 */
public class MiaoshaUserKey extends BasePrefix {

    /**
     * token过期时间为两天
     */
    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
