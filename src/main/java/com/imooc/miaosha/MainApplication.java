package com.imooc.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 主程序
 *
 * @author Administrator
 */
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);

    }


    /**
     * 运行war程序时先继承SpringBootServletInitializer
     * 并且复写此方法
     *
     * @param builder
     * @return
     */
    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }*/
}
