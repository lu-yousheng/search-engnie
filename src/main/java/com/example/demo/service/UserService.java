package com.example.demo.service;

import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    //查询用户是否已经存在
    public String selectByName(String username){
        return userMapper.selectByName(username);
    }

    //用户注册
    public int reg(Userinfo userinfo){
        return userMapper.reg(userinfo);
    }

    //根据用户名返回用户信息
    public Userinfo getUserByName(String username){
        return userMapper.getUserByName(username);
    }
}
