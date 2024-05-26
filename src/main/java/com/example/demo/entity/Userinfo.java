package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Userinfo {
    private int id;
    private String username;
    private String password;
    private String nike;
    private String photo;
    private Date createtime;
    private Date updatetime;
    private int docount;
    private UserRole userRole;
    private String contact;
}
