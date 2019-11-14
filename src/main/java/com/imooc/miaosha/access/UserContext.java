package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoshaUser;

/**
 * 使用ThreadLocal存储当前线程的用户信息
 *
 * @author Administrator
 */
public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

}
