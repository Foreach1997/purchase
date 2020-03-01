package com.pu.purchase.controller;


import com.pu.purchase.entity.User;
import com.pu.purchase.service.impl.UserServiceImpl;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.vo.UserVo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userServiceImpl;

    /**
     * 登录
     */
    @GetMapping("/login")
    public Object login(String email, String password, HttpServletRequest request) {
        return userServiceImpl.login(email, password, request);
    }

    /**
     * 登出
     */
    @GetMapping("/loginOut")
    public Object loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return RepResult.repResult(0, "登出", null);
    }




    /**
     * 加载用户列表数据
     */
    @GetMapping("/loadAllUser")
    public Object loadAllUser(UserVo userVo) {
        return userServiceImpl.queryAllUser(userVo);
    }


    /**
     * 添加用户
     *
     * @param userVo
     * @return
     */
    @GetMapping("/addUser")
    public Object addUser(UserVo userVo) {
        return userServiceImpl.insertSelective(userVo);
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @GetMapping("/updateUser")
    public Object updateUser(User user) {
        return userServiceImpl.updateByPrimaryKeySelective(user);
    }

    /**
     * 删除用户
     */
    @GetMapping("/deleteUser")
    public Object deleteUser(String id) {
        return userServiceImpl.deleteByPrimaryKey(id);
    }




}
