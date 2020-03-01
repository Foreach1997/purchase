package com.pu.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pu.purchase.config.BizException;
import com.pu.purchase.dto.UserDto;
import com.pu.purchase.entity.User;
import com.pu.purchase.mapper.UserMapper;
import com.pu.purchase.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pu.purchase.util.DateUtils;
import com.pu.purchase.util.RepResult;
import com.pu.purchase.util.WebUtils;
import com.pu.purchase.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 
 * @since 2020-03-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    public Object login(String email, String password, HttpServletRequest request) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getEmail,email)
                .eq(User::getPassword,password));
        if (user != null){
            HttpSession session= request.getSession();
            session.setAttribute("user",user);
            return RepResult.repResult(0,"登录成功",user);
        }
        throw new BizException("检查账号或邮箱");
    }



    public Object queryAllUser(UserVo userVo) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(StringUtils.isNotBlank(userVo.getPhone()), User::getPhone, userVo.getPhone())
                .eq(StringUtils.isNotBlank(userVo.getUserName()), User::getUserName, userVo.getUserName());
        IPage<User> userPage = userMapper.selectPage(new Page<>(userVo.getPage(),userVo.getLimit()), queryWrapper);
        List<UserDto> dtoList = userPage.getRecords().stream().map(user -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user,userDto);
            userDto.setCreateTime(DateUtils.dateFrString(user.getCreateTime()));
            return userDto;
        }).collect(Collectors.toList());
        return RepResult.repResult(0, "查询成功", dtoList, (int)userPage.getTotal());
    }

    public Object insertSelective(User record) {

        if (null == record) {
            throw new BizException("添加数据为空");
        }
        record.setCreateName(WebUtils.getCurrentUserName());
        record.setPassword("123456");
        record.setCreateTime(LocalDateTime.now());
        User email = userMapper.selectOne(new QueryWrapper<User>().eq("email", record.getEmail()));
        if(null!=email){
            return RepResult.repResult(1, "邮箱已存在", null);
        }
        if (1 != userMapper.insert(record)) {
            throw new BizException("添加用户数据失败");
        }

        return RepResult.repResult(0, "添加成功", null);
    }

    public Object updateByPrimaryKeySelective(User record) {
        if (null == record) {
            throw new BizException("修改数据为空");
        }
        if (1 != userMapper.update(record,new QueryWrapper<User>().eq("id",record.getId()))) {
            throw new BizException("修改用户数据失败");
        }
        return RepResult.repResult(0, "修改成功", null);
    }


    public Object updatePassword(String password) {
        if (null == password) {
            throw new BizException("修改数据为空");
        }
        User user_name = userMapper.selectOne(new QueryWrapper<User>().eq("user_name", WebUtils.getCurrentUserName()));
        user_name.setPassword(password);
        if (1 != userMapper.updateById(user_name)) {
            throw new BizException("修改密码失败");
        }
        return RepResult.repResult(0, "修改成功", null);
    }


    public Object deleteByPrimaryKey(String id) {
        if (null == id) {
            throw new BizException("删除id为空");
        }
        if (1 != userMapper.deleteById(id)) {
            throw new BizException("删除用户数据失败");
        }
        return RepResult.repResult(0, "删除成功", null);
    }

}
