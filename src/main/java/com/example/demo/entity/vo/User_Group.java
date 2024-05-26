package com.example.demo.entity.vo;

import com.example.demo.entity.UserRole;
import lombok.Data;

import java.util.Date;
@Data
public class User_Group {
    private int id;
    private UserRole role;
    private Date joined_at;
    private String username;
    private int user_id;
    private String contact;
}
