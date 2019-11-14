package com.imooc.miaosha.access;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;

/**
 * 限流拦截器
 *
 * @author Administrator
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    /**
     * 在controller方法执行之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //判断是否是一个controller层的方法
        if (handler instanceof HandlerMethod) {
            //获取登录用户
            MiaoshaUser user = getUser(request, response);
            //向ThreadLocal中保存登录用户信息
            UserContext.setUser(user);

            //获取controller层方法上的注解
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                //如果方法没有加这个注解，则允许执行
                return true;
            }
            //获取到注解中的信息
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            //获取请求路径
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            } else {
                //do nothing
            }
            AccessKey ak = AccessKey.withExpire(seconds);
            //获取某个链接被点击的次数
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < maxCount) {
                //加一操作
                redisService.incr(ak, key);
            } else {
                //给前端返回操作太频繁提示信息
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 发送json信息
     *
     * @param response
     * @param cm
     * @throws Exception
     */
    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 根据cookie中的token查询用户
     *
     * @param request
     * @param response
     * @return
     */
    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        //cookie有可能在参数中，也有可能不在参数中
        // 从参数中获取token
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        //从request中获取token
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        //如果都没有，则返回null
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        //拿到token
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        //根据token获取秒杀用户信息
        return userService.getByToken(response, token);
    }

    /**
     * 从request中通过cookie名称获取cookie值
     *
     * @param request
     * @param cookiName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        //从request中获取所有cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        //遍历所有cookie，找到对应cookie名称的cookie值
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
