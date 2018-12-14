package com.nbcb.myron.bsen.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 重写添加拦截器方法并添加配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截哪些路径("/**":代表拦截所有路径.("/bs/indexdata", "/logout","/error","docs.html"):多参数传递用逗号分隔)
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/bs/myinfo");
    }
}

