package com.imooc.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.CodeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;

/**
 * 登录控制器
 *
 * @author Administrator
 */
@Controller
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/")
    public String welcome() {
        return "forward:/login/to_login";
    }


    /**
     * 跳转到登录模板
     *
     * @return
     */
    @RequestMapping("/login/to_login")
    public String toLogin() {
        return "login";
    }

    /**
     * 执行登录操作
     * 使用@Valid注解进行参数校验
     *
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/login/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(MiaoshaUser user, HttpServletResponse response, @Valid LoginVo loginVo) {
        if (user == null) {
            //登录
            userService.login(response, loginVo);
        }

        //登录
        //userService.login(response, loginVo);
        return Result.success(true);
    }
}
