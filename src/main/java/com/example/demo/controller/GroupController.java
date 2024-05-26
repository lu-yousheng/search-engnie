package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.User_GroupsInfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.entity.vo.User_Group;
import com.example.demo.service.GroupService;
import com.example.demo.service.User_GroupsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupService groupService;
    @Resource
    private User_GroupsService groupsService;

    @RequestMapping("/add")
    public AjaxResult add(HttpServletRequest request, GroupInfo groupInfo){
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        if (userinfo == null) {
            return AjaxResult.fail(-1,"用户未登录");
        }
        //查询群名称是否已经存在
        if (groupService.getGroupName(groupInfo.getGroup_name()) != null) {
            return AjaxResult.fail(-3,"群名称重复");
        }
        groupInfo.setUser_name(userinfo.getUsername());
        groupInfo.setUserId(userinfo.getId());
        groupService.add(groupInfo);

        User_GroupsInfo user_groupsInfo = new User_GroupsInfo();
        user_groupsInfo.setUser_id(userinfo.getId());
        user_groupsInfo.setGroup_id(groupService.getGroupIdByname(groupInfo.getGroup_name()));
        user_groupsInfo.setRole(UserRole.ADMIN);

        return AjaxResult.success(groupsService.add(user_groupsInfo));
    }

    @RequestMapping("/selflist")
    public AjaxResult selflist(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        if (userinfo ==null) {
            return AjaxResult.fail(-1,"用户未登录");
        }
        List<GroupInfo> groupInfoList = groupsService.getAllGroupsByOnwer(userinfo.getId(),"ADMIN");
        List<GroupInfo> groupInfoListAll = groupsService.getAllGroupsByOnwer(userinfo.getId(),"USER");
        Map<String, List<GroupInfo>> groupInfoMap = new HashMap<>();
        groupInfoMap.put("ownedGroups", groupInfoList);
        groupInfoMap.put("allGroups", groupInfoListAll);
        return AjaxResult.success(groupInfoMap);
    }

    @RequestMapping("/userlist")
    public AjaxResult userlistIngroup(Integer group_id){
        if (group_id == null) {
            return AjaxResult.fail(-1,"没有获取到群组id");
        }
        List<User_Group> user_groupList = groupsService.userListIngroup(group_id);
        return AjaxResult.success(user_groupList);
    }

    @RequestMapping("/deleteuser")
    public AjaxResult deleteUser(Integer user_id){
        if (groupsService.deleteUser(user_id) > 0) {
            return AjaxResult.success("OK");
        }
        return AjaxResult.fail(-1,"删除失败");
    }

    @RequestMapping("/groupslist")
    public AjaxResult groupslist() {
        List<GroupInfo> groupInfoList = groupsService.getAllGroup();
        return AjaxResult.success(groupInfoList);
    }

    @RequestMapping("/joingroup")
    public AjaxResult joingroup(HttpServletRequest request,int group_id){
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        if (userinfo == null) {
            return AjaxResult.fail(-1,"用户未登录");
        }
        User_GroupsInfo userGroupsInfo = new User_GroupsInfo();
        userGroupsInfo.setGroup_id(group_id);
        userGroupsInfo.setUser_id(userinfo.getId());
        userGroupsInfo.setRole(UserRole.USER);
        return AjaxResult.success(groupsService.insertUser(userGroupsInfo));

    }


    @RequestMapping("/deletgroup")
    public AjaxResult deletGroup(int group_id){
        if (groupsService.deletGroup(group_id) > 0){
            if (groupService.deletGroup(group_id) > 0) {
                return AjaxResult.success("OK");
            }
        }
         return AjaxResult.fail(-1,"删除失败");
    }
    @RequestMapping("/isjoin")
    public AjaxResult isJoin(HttpServletRequest request,int groupId){
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        assert userinfo != null;
        if (groupsService.idJoin(userinfo.getId(),groupId) != null){
            return AjaxResult.success("true");
        }
        return AjaxResult.fail(-1,"false");
    }
}
