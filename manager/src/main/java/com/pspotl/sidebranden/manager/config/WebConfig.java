package com.pspotl.sidebranden.manager.config;

import com.pspotl.sidebranden.manager.filter.LoginFilter;
import com.pspotl.sidebranden.manager.interceptor.BearerAuthInterceptor;
import com.pspotl.sidebranden.manager.util.ResponseUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

@Configuration
@ComponentScan
public class WebConfig implements WebMvcConfigurer {

//    @Autowired
//    CertificationInterceptor certificationInterceptor;

    @Autowired
    BearerAuthInterceptor bearerAuthInterceptor;

    @Bean
    public ResponseUtility utility() { return  new ResponseUtility(); }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost", "https://www.tetete.com", "https://tetete.com","http://docker.for.mac.localhost","https://whive-dev-alb-1269990993.ap-northeast-2.elb.amazonaws.com","https://dev.w-hive.io","http://w-hive.io","https://w-hive.io")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Content-Type","X-AUTH-TOKEN","authorization", "Access-Control-Allow-Headers","Access-Control-Allow-Credentials"," X-Ajax-call","Access-Control-Request-Method")
                .exposedHeaders("Content-Dispostion","Access-Control-Allow-Headers","X-AUTH-TOKEN", "Access-Control-Allow-Headers","Access-Control-Allow-Credentials", "authorization"," X-Ajax-call","Access-Control-Request-Method").allowCredentials(true).maxAge(3600);
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(-1);
        return multipartResolver;
    }

    //regist websquare dispatcher
    @Bean
    public ServletRegistrationBean<websquare.http.DefaultRequestDispatcher> getServletRegistrationBean()
    {
        ServletRegistrationBean<websquare.http.DefaultRequestDispatcher> websquareDispatcher = new ServletRegistrationBean<>(new websquare.http.DefaultRequestDispatcher());
        websquareDispatcher.addUrlMappings("*.wq");
        return websquareDispatcher;
    }

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(ContentNegotiationManager contentNegotiationManager) {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(contentNegotiationManager);
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(1);
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilerBean() {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.setFilter(new LoginFilter());
        registrationBean.addUrlPatterns("/manager/*","/builder/*");

        return registrationBean;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bearerAuthInterceptor);
    }
}
