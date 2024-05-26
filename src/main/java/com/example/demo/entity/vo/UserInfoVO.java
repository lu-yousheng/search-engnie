package com.example.demo.entity.vo;

import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.Userinfo;
import lombok.Data;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;
@Data
public class UserInfoVO extends Userinfo {
    @ManyToMany
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<GroupInfo> groups;
}
