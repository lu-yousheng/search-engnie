package com.example.demo.entity.vo;

import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.Userinfo;
import lombok.Data;

import javax.persistence.ManyToMany;
import java.util.List;
@Data
public class GroupInfoVO extends GroupInfo {
    @ManyToMany(mappedBy = "groups")
    private List<Userinfo> users;
}
