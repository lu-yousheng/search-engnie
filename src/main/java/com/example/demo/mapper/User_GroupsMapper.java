package com.example.demo.mapper;

import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.User_GroupsInfo;
import com.example.demo.entity.vo.User_Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface User_GroupsMapper {
    public int add(User_GroupsInfo user_groupsInfo);
    public List<GroupInfo> getAllGroupsByOnwer(int id, String role);
    public List<User_Group> userListIngroup(int group_id);
    public List<GroupInfo> getAllGroup();
    public int insertUser(User_GroupsInfo user_groupsInfo);
    public int deletGroup(int group_id);
    public int deleteUser(int id);
    public User_GroupsInfo idJoin(int user_id,int group_id);
    public String isAdmin(int user_id,int group_id);
}
