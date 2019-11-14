package com.imooc.miaosha.redis;

/**
 * 基础前缀抽象类
 * 定义一些公共方法
 *
 * @author Administrator
 */
public abstract class BasePrefix implements KeyPrefix {

    /**
     * 过期时间
     */
    private int expireSeconds;

    /**
     * 前缀
     */
    private String prefix;

    public BasePrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {//默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //实际前缀为类名拼接传入的前缀
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

}
