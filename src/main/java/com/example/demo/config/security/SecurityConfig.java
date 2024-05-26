package com.example.demo.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /*
    配置登录授权
     */

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/files/**").permitAll()
                .antMatchers("/group/**").permitAll()
                .antMatchers("/reg.html").permitAll()
                .antMatchers("/userlogin.html").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/upfile-user.html").permitAll()
                .antMatchers("/messages.html").permitAll()
                .antMatchers("/fileDownload/**").permitAll()
                .antMatchers("/addgroup.html").permitAll()
                .antMatchers("/groups.html").permitAll()
                .antMatchers("/joingroup.html").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .and()
                .logout().logoutSuccessUrl("/").permitAll();
    }



}
