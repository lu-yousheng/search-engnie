package com.example.demo.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class PasswordUtils {
    //1,加盐并生成密码
    public static String encrypt(String password) {
        //a,盐值
        String salt = UUID.randomUUID().toString().replace("-","");
        //b,生成加盐之后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        //c.生成最终密码  约定格式:32位盐值+$+32位加盐之后的密码
        String finalPassword = salt + "$" +saltPassword;
        return finalPassword;
    }

    /**
     *
     * @param password 明文
     * @param salt 固定的盐值
     * @return 最终密码
     */
    //2,生成加盐密码,是方法1的重载
    public static String encrypt(String password,String salt){
        //1,生成一个加盐之后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        //2,生成最终的密码
        String finalPassword = salt + "$" +saltPassword;
        return finalPassword;
    }
    //3,验证密码
    public static boolean check(String inputPassword, String finalPassword) {
        if (StringUtils.hasLength(inputPassword) && StringUtils.hasLength(finalPassword) && finalPassword.length() == 65){
            //得到盐值
            String salt = finalPassword.split("\\$")[0];
            //
            String confirm = PasswordUtils.encrypt(inputPassword,salt);
            //3,对比两个是否相同
            return confirm.equals(finalPassword);
        }
        return false;
    }

    public static void main(String[] args) {
        String password = "123";
        String finalPassword =  PasswordUtils.encrypt(password);
        String salt = finalPassword.split("\\$")[0];

        String pas = "123";
        String fin =  encrypt(pas,salt);
        System.out.println(fin.equals(finalPassword));
    }
}
