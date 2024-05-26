package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupInfo {
    private int group_id;
    private String group_name;
    private int userId;
    private String user_name;
    private Date createtime;
    private String description;
    private int count_Max;
}
