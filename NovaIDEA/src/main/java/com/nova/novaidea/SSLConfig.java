package com.nova.novaidea;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;

import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动定向https
 * @author Christy
 * @version 1.0
 * @since 2021/9/17 01:14
 */
@Configuration
public class SSLConfig {
    @Bean
    public Connector connector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        // Connector监听的http的端口号
        connector.setScheme("http");
        connector.setPort(8002);
        connector.setSecure(false);
        // 监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(8003);
        return connector;
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        // 对http请求添加安全性约束，将其转换为https请求
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(connector());
        return tomcat;
    }

}