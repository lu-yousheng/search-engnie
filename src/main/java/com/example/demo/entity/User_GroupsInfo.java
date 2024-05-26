package com.example.demo.entity;

import lombok.Data;

import java.util.Date;
@Data
public class User_GroupsInfo {
    private int id;
    private int user_id;
    private int group_id;
    private UserRole role;
    private Date joined_at;
}
