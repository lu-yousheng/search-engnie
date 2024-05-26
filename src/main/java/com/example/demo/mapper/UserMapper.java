package com.example.demo.mapper;

import com.example.demo.entity.Userinfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public String selectByName(String username);//通过用户名查询用户
    public int reg(Userinfo userinfo);//用户注册
    public Userinfo getUserByName(String username);//根据用户名返回信息
}
