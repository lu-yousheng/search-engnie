package com.example.demo.common;

import com.example.demo.entity.Userinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSessionUtils {
    /**
     * 得到当前的登录用户
     * @param request
     * @return
     */
    public static Userinfo getSessUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null &&
                session.getAttribute(AppVariable.USER_SESSION_KEY) != null) {
            //用户已登录
            return (Userinfo) session.getAttribute(AppVariable.USER_SESSION_KEY);
        }
        return null;
    }
}
