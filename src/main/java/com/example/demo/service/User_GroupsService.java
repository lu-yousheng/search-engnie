package com.example.demo.service;

import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.User_GroupsInfo;
import com.example.demo.entity.vo.User_Group;
import com.example.demo.mapper.User_GroupsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class User_GroupsService {
    @Resource
    private User_GroupsMapper groupsMapper;

    public int add(User_GroupsInfo groupsInfo){
        return groupsMapper.add(groupsInfo);
    };

    public List<GroupInfo> getAllGroupsByOnwer(int id,String role){
        return groupsMapper.getAllGroupsByOnwer(id,role);
    }

    public List<User_Group> userListIngroup(int groups_id){
        return groupsMapper.userListIngroup(groups_id);
    }
    public List<GroupInfo> getAllGroup(){
        return groupsMapper.getAllGroup();
    };

    public int insertUser(User_GroupsInfo user_groupsInfo) {
        return groupsMapper.insertUser(user_groupsInfo);
    }

    public int deletGroup(int group_id){
        return groupsMapper.deletGroup(group_id);
    }
    public int deleteUser(int id){
        return groupsMapper.deleteUser(id);
    }
    public User_GroupsInfo idJoin(int user_id,int group_id){
        return groupsMapper.idJoin(user_id,group_id);
    }
    public String isAdmin(int user_id,int group_id){
        return groupsMapper.isAdmin(user_id,group_id);
    }
}
