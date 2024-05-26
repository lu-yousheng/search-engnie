package com.example.demo.service;

import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.User_GroupsInfo;
import com.example.demo.mapper.GroupMapper;
import com.example.demo.mapper.User_GroupsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GroupService {
    @Resource
    private GroupMapper groupMapper;

    public int add(GroupInfo groupInfo){
        return groupMapper.add(groupInfo);
    }

    public int getGroupIdByname(String name) {
        return groupMapper.getGroupIdByname(name);
    }
    public String getGroupName(String name){
        return groupMapper.getGroupName(name);
    };
    public int deletGroup(int group_id){
        return groupMapper.deletGroup(group_id);
    }
    public String getNameById(int groupId){
        return groupMapper.getNameById(groupId);
    };
    public String getGroupUserNameById(int group_id){
        return groupMapper.getGroupUserNameById(group_id);
    };
}
