package com.imooc.miaosha.controller;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private UserService userService;



    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender sender;

    @RequestMapping("/headers/mq")
    @ResponseBody
    public Result<String> headersMq(Model model){
        sender.sendHeaders("hello imooc");
        return Result.success("hello");
    }



    @RequestMapping("/fanout/mq")
    @ResponseBody
    public Result<String> fanoutMq(Model model){
        sender.sendFanout("hello imooc");
        return Result.success("hello");
    }


    @RequestMapping("/topic/mq")
    @ResponseBody
    public Result<String> topicMq(Model model){
        sender.sendTopic("hello imooc");
        return Result.success("hello");
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(Model model){
        sender.send("hello imooc");
        return Result.success("hello");
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Joshua");
        return "hello";
    }


    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }



    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById,"" + 1, user);
        return Result.success(true);
    }


    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(UserKey.getById , ""+1, User.class);

        return Result.success(user);
    }

}
