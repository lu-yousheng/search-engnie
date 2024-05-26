package com.example.demo.mapper;

import com.example.demo.entity.GroupInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMapper {
    public int add(GroupInfo groupInfo);
    public int getGroupIdByname(String name);
    public String getGroupName(String name);
    public int deletGroup(int group_id);
    public String getNameById(int groupId);
    public String getGroupUserNameById(int group_id);
}
