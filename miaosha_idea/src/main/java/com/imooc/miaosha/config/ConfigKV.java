package com.imooc.miaosha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 获取配置文件的键值对
 */
@Component
@ConfigurationProperties(prefix="server")
@PropertySource("classpath:application.properties")
public class ConfigKV {

    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
