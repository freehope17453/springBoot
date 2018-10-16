package com.example.myproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloWorldController {

	@Autowired
	RedisTemplate<String,String> redisTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@RequestMapping("/hello")
	public String getHello() {
		// 具体使用
        redisTemplate.opsForList().leftPush("user:list", "name");
        stringRedisTemplate.opsForValue().set("user:name", "张三");
		return "Hello World ";
	}
}
