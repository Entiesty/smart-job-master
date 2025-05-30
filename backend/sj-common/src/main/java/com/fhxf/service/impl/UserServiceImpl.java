package com.fhxf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.config.JwtProperties;
import com.fhxf.domain.dto.register.EnterPriseRegisterFormDTO;
import com.fhxf.domain.dto.base.LoginFormDTO;
import com.fhxf.domain.dto.base.RegisterFormDTO;
import com.fhxf.domain.dto.register.WorkerRegisterFormDTO;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.enums.UserStatusEnum;
import com.fhxf.domain.po.Enterprises;
import com.fhxf.domain.po.Worker;
import com.fhxf.domain.vo.EnterpriseVo;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.domain.po.User;
import com.fhxf.domain.vo.UserRegisterVO;
import com.fhxf.domain.vo.WokerVo;
import com.fhxf.global.exception.BadRequestException;
import com.fhxf.service.UserService;
import com.fhxf.mapper.UserMapper;
import com.fhxf.utils.BeanUtils;
import com.fhxf.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
* @author fhxf111
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-04-29 23:49:35
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    private final  EnterprisesServiceImpl enterprisesService;
    private final WorkerServiceImpl workerService;

    public UserVo login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user;
        try {
            user = lambdaQuery().eq(User::getUsername, username).one();
        }catch (TooManyResultsException e){
            throw new BadRequestException("用户已存在");
        }
        // 4.校验密码
        if (user==null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(),user.getRole(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserVo userVo = BeanUtils.copyBean(user, UserVo.class);
  
        userVo.setToken(token);
        return userVo;
    }

    public UserRegisterVO register(RegisterFormDTO registerFormDTO) {
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        User user = BeanUtil.copyProperties(registerFormDTO, User.class, "password","role");
        user.setRole(UserRoleEnum.getByCode(registerFormDTO.getRole()));
        user.setUsername(username).setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatusEnum.ACTIVE);
        baseMapper.insert(user);
        // 封装VO返回
        UserRegisterVO vo = new UserRegisterVO();
        vo.setIsSuccess("success");
        return vo;
    }
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterVO workerRegister(WorkerRegisterFormDTO registerFormDTO) {
        register(registerFormDTO);
        Long id = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, registerFormDTO.getUsername())).getId();
        registerFormDTO.setUserId(id);
        workerService.createWorker(registerFormDTO);
        return new UserRegisterVO("success");
    }
    public UserRegisterVO enterpriseRegister(EnterPriseRegisterFormDTO registerFormDTO) {
        register(registerFormDTO);
        Long id = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, registerFormDTO.getUsername())).getId();
        registerFormDTO.setUserId(id);
        enterprisesService.createEnterprise(registerFormDTO);
        return new UserRegisterVO("success");
    }
}




