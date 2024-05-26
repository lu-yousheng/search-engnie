package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.PasswordUtils;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Userinfo;
import com.example.demo.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;




@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;


    @RequestMapping("/reg")
    public AjaxResult reg(Userinfo userinfo){
        //检查前端传入的信息是否异常
        if (userinfo == null || !StringUtils.hasLength(userinfo.getUsername())
                || !StringUtils.hasLength(userinfo.getPassword()) || "".equals(userinfo.getUsername())){
                return AjaxResult.fail(-1,"信息输入异常");

        }

        //检查用户名是否已经被注册
        if (userService.selectByName(userinfo.getUsername()) != null){
            //用户名已经存在
            return AjaxResult.fail(-2,"用户名已经存在");
        }
        //进行信息录入
        userinfo.setUserRole(UserRole.USER);
        userinfo.setPassword(PasswordUtils.encrypt(userinfo.getPassword()));
        return AjaxResult.success(userService.reg(userinfo));
    }


    //用户登录
    @RequestMapping("/userlogin")
    public AjaxResult userlogin(HttpServletRequest request, String username,String password) {
        //非空校验和擦书有效性校验
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return AjaxResult.fail(-1,"非法参数");
        }
        Userinfo userinfo = userService.getUserByName(username);
        if (userinfo != null && userinfo.getId() > 0) {
            //有效用户
            //判断密码是否相等
            String salt = userinfo.getPassword().split("\\$")[0];//得到盐值
            String finalPassword = PasswordUtils.encrypt(password,salt);
            if (finalPassword.equals(userinfo.getPassword())) {
                //登录成功
                userinfo.setPassword(""); //返回前端之前隐藏敏感信息
                //将用户存储到session
                HttpSession session = request.getSession(); //默认为TRUE
                session.setAttribute("USER_SESSION_KEY",userinfo);
                return AjaxResult.success(userinfo);
            }
        } else {
            return AjaxResult.success(0,null);
        }
        return AjaxResult.fail(-1,"非法参数");

    }

    @RequestMapping("/showinfo")
    public AjaxResult showInfo(HttpServletRequest request){
        Userinfo user = UserSessionUtils.getSessUser(request);
        if (user == null) {
            return AjaxResult.fail(-1,"用户未登录");
        }
        Userinfo userinfo = userService.getUserByName(user.getUsername());
        user.setPassword(null);
        return AjaxResult.success(user);
    }

    @RequestMapping("/islogin")
    public AjaxResult isLogin(HttpServletRequest request) {
        Userinfo userinfo = UserSessionUtils.getSessUser(request);
        if (userinfo != null){
            return  AjaxResult.success("ture");
        }
        return AjaxResult.fail(-1,"false");
    }


}
