package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexPatterns;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.redis.connection.ReactiveClusterScriptingCommands;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Mapper
    UserMapper userMapper;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)){
            // 2. 如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }

        // 3. 如果符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4. 保存验证码到session
        session.setAttribute("code", code);

        // 5. 发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);

        // 返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 0.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(loginForm.getPhone())){
            // 如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }
        // 1. 校验验证码
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            return Result.fail("验证码错误");
        }

        // 2. 根据手机号查询
        User user = query().eq("phone", phone).one();
        // 另一种写法
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("phone", phone);
        // User user = userMapper.selectOne(queryWrapper);

        // 3. 用户不存在
        if (user == null) {
            // 创建用户
            user = createUserWithPhone(phone);
        }

        // 5. 保存到session
        session.setAttribute("user", user);
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        // 1. 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));

        // 2. 保存用户
        save(user);

        return user;
    }
}
