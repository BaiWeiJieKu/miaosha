package com.imooc.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {


    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 根据id获取用户
     * 使用对象缓存
     *
     * @param id
     * @return
     */
    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        //查数据库
        user = miaoshaUserDao.getById(id);
        if (user != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        }
        return user;
    }

    /**
     * 修改密码
     * 用户信息改变要对缓存进行修改
     *
     * @param token
     * @param id
     * @param formPass
     * @return
     */
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }


    /**
     * 通过token获取秒杀用户信息
     *
     * @param response
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //从Redis缓存中获取秒杀用户信息
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }


    /**
     * 登录
     *
     * @param response
     * @param loginVo
     * @return
     */
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        //视图对象为空，抛出自定义全局异常，由自定义的异常处理器处理
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //登录成功生成一个随机cookie，返回给浏览器，在以后的请求中携带token寻找用户
        //通过随机数生成一个token
        String token = UUIDUtil.uuid();
        //把token设置到cookie
        addCookie(response, token, user);
        return true;
    }

    /**
     * 添加token到cookie
     *
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        //向Redis缓存中写入token和用户信息
        //键是token，值是用户信息
        redisService.set(MiaoshaUserKey.token, token, user);
        //把token放入到cookie中
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        //设置cookie有效期，和Redis缓存有效期一致
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        //把cookie添加到response中
        response.addCookie(cookie);
    }

}
