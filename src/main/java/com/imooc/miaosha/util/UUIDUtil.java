package com.imooc.miaosha.util;

import java.util.UUID;

/**
 * 随机数生成工具类
 *
 * @author Administrator
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
