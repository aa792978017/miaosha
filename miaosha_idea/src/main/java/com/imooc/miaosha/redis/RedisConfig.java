package com.imooc.miaosha.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * redis服务配置类
 */
@Component
@ConfigurationProperties(prefix="redis") //获取配置文件里面以redis开头的配置属性
public class RedisConfig {
	//ip
	private String host; //ip
	//端口号
	private int port;
	//过期时间
	private int timeout;//秒
	//密码
	private String password;
	//最大连接数
	private int poolMaxTotal;
	//最大空闲连接数
	private int poolMaxIdle;
	//最长等待时间
	private int poolMaxWait;//秒


	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPoolMaxTotal() {
		return poolMaxTotal;
	}
	public void setPoolMaxTotal(int poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
	}
	public int getPoolMaxIdle() {
		return poolMaxIdle;
	}
	public void setPoolMaxIdle(int poolMaxIdle) {
		this.poolMaxIdle = poolMaxIdle;
	}
	public int getPoolMaxWait() {
		return poolMaxWait;
	}
	public void setPoolMaxWait(int poolMaxWait) {
		this.poolMaxWait = poolMaxWait;
	}
}
