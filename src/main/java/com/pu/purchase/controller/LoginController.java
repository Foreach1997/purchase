package com.pu.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pu.purchase.entity.User;
import com.pu.purchase.mapper.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("login")
public class LoginController {

    @Resource
    private UserMapper userMapper;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public ModelAndView login(String email, String password, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {

            User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                    .eq(User::getEmail,email)
                    .eq(User::getPassword,password));
            if (user != null){
                HttpSession session= request.getSession();
                session.setAttribute("user",user);
                modelAndView.setViewName("main");
                return modelAndView;
            }
            modelAndView.addObject("error", "用户名或密码错误");
            modelAndView.setViewName("index");
            return modelAndView;
        } catch (Exception e) {
            modelAndView.addObject("error", "用户名或密码错误");
            modelAndView.setViewName("index");
            return modelAndView;
        }

    }
}
