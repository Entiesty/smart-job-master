package com.fhxf.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fhxf.domain.dto.register.EnterPriseRegisterFormDTO;
import com.fhxf.domain.dto.base.LoginFormDTO;
import com.fhxf.domain.dto.base.VerifyCaptchaDTO;
import com.fhxf.domain.dto.register.WorkerRegisterFormDTO;
import com.fhxf.domain.enums.CertificateStatusEnum;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.po.*;
import com.fhxf.domain.vo.EnterpriseVo;
import com.fhxf.domain.vo.UserRegisterVO;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.domain.vo.WokerVo;
import com.fhxf.global.component.RedisComponent;
import com.fhxf.domain.constans.Constants;
import com.fhxf.global.exception.BadRequestException;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.global.ResponseResult;
import com.fhxf.service.impl.*;
import com.fhxf.utils.UserContext;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController  extends  ABaseController{

    private  final UserServiceImpl userService;
    private  final RedisComponent redisComponent;
    private  final WorkerServiceImpl workerService;
    private  final CertificatesServiceImpl certificatesService;
    private  final EnterprisesServiceImpl enterprisesService;
    private final FileController fileController;
    private final ReviewServiceImpl reviewService;
    //验证码
    @GetMapping("/captcha")
    public ResponseResult captcha() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String captchaBase64 = captcha.toBase64();
        String captchaKey = redisComponent.saveCheckCode(code);
        Map<String, String> result = new HashMap<>();
        result.put("captchaImg", captchaBase64);
        result.put("captchaKey", captchaKey);
        return success(result);
    }
    @PostMapping("/verifyCaptcha")
    public ResponseResult verifyCaptcha(@RequestBody VerifyCaptchaDTO captchaDTO ) {
        if (!captchaDTO.getCaptcha().equalsIgnoreCase(redisComponent.getCheckCode(captchaDTO.getCaptchaKey()))) {
            throw new BusinessException("验证码错误!");
        }
        return success();
    }
    @GetMapping("/getAvatar/{userId}")
    public ResponseResult getAvatar(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在!");
        }
        return success(user.getAvatar());
    }
    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo( ) throws BusinessException {
        Long userId = UserContext.getUser();
        User user = userService.getById(userId);
        if(user==null){
            throw new BusinessException("用户不存在!");
        }
        UserVo userVo =BeanUtil.copyProperties(user, UserVo.class);
        if(user.getRole().equals(UserRoleEnum.ENTERPRISE)){
            Enterprises enterprise = enterprisesService.getOne(new LambdaQueryWrapper<Enterprises>().eq(Enterprises::getUserId, user.getId()));
            userVo.setEnterprise(BeanUtil.copyProperties(enterprise, EnterpriseVo.class));
        }else if(user.getRole().equals(UserRoleEnum.EMPLOYER)){
            Worker worker = workerService.getOne(new LambdaQueryWrapper<Worker>().eq(Worker::getUserId, user.getId()));
            userVo.setWorker(BeanUtil.copyProperties(worker, WokerVo.class,"label"));
            userVo.getWorker().setLabel(worker.getJsonLabel());
            userVo.getWorker().setWorkDay(worker.getJsonWorkDay());
            userVo.getWorker().setCertificates(certificatesService.list(new LambdaQueryWrapper<Certificates>().eq(Certificates::getUserId,userId)));
        }else{
            throw new BadRequestException("未识别的用户角色");
        }
        String token = redisComponent.getUserId(userId);
        userVo.setToken(token);
        return success(userVo);
    }
    //登录
    @PostMapping("/login")
    public ResponseResult<UserVo> Login(@RequestBody @Validated LoginFormDTO loginFormDTO) throws BusinessException {
        // 验证码校验
        VerifyCaptchaDTO verifyCaptchaDTO = BeanUtil.copyProperties(loginFormDTO, VerifyCaptchaDTO.class);
        verifyCaptcha(verifyCaptchaDTO);
        UserVo login = userService.login(loginFormDTO);
        // 保存token到redis
        redisComponent.saveUserToken(login, Constants.REDIS_KEY_TOKEN);
        String token = redisComponent.getUserId(login.getId());
        if(!StrUtil.isEmpty(token)){
            redisComponent.removeUserToken(token, Constants.REDIS_KEY_TOKEN);
        }
        redisComponent.saveUserId(login.getId(),login.getToken());
        // 保存token到cookie
        saveTokenToCookie(login.getToken(), Constants.TOKEN_WEB);

        return success("登录成功",login);
    }
    @GetMapping("/autoLogin")
    public ResponseResult autologin() throws BusinessException {
        UserVo login = getUserByTokenFromCookie();
        if (null == login) {
            throw new BusinessException("登录过期,请重新登录!");
        }
        return success("登录成功",login);
    }
    @GetMapping("/logout")
    public ResponseResult logout() throws BusinessException {
        boolean b = removeUserByTokenFromCookie();
        if (!b) {
            throw new BusinessException("token不存在!或cookie不存在!");
        }
        return success("退出登录!");
    }
    //注册
    @PostMapping("/workerRegister")
    public ResponseResult<UserRegisterVO> WorkerRegister(@RequestBody @Validated WorkerRegisterFormDTO registerFormDTO) throws BusinessException {
        // 验证码校验
        VerifyCaptchaDTO verifyCaptchaDTO = BeanUtil.copyProperties(registerFormDTO, VerifyCaptchaDTO.class);
        verifyCaptcha(verifyCaptchaDTO);
        userService.workerRegister(registerFormDTO);
        return success();
    }
    @PostMapping("/enterpriseRegister")
    public ResponseResult<UserRegisterVO> enterpriseRegister(@RequestBody @Validated EnterPriseRegisterFormDTO registerFormDTO) throws BusinessException {
        // 验证码校验
        VerifyCaptchaDTO verifyCaptchaDTO = BeanUtil.copyProperties(registerFormDTO, VerifyCaptchaDTO.class);
        verifyCaptcha(verifyCaptchaDTO);

        userService.enterpriseRegister(registerFormDTO);
        return success();
    }
    //个人中心
    @PutMapping("/updateUserInfo")
    public ResponseResult updateUserInfo(@RequestBody UserVo userVo) throws BusinessException {
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, userVo.getId()));
        if(one == null){
            throw new BusinessException("用户不存在!");
        }
        if(!one.getId().equals(UserContext.getUser())){
            throw new BusinessException("无权限!");
        }
        UserVo login = getUserByTokenFromCookie();
        User user = BeanUtil.copyProperties(userVo, User.class);
        userService.updateById(user);
        userVo.setRole(login.getRole());
        userVo.setEnterprise(login.getEnterprise());
        userVo.setWorker(login.getWorker());
        redisComponent.saveUserToken(userVo, Constants.REDIS_KEY_TOKEN);
        return success(userVo);
    }
    @PutMapping("/updateUserCertificates")
    public ResponseResult updateUserCertificates(@RequestBody Certificates certificates ) throws BusinessException {
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, certificates.getUserId()));
        if(one == null){
            throw new BusinessException("用户不存在!");
        }
        if(!one.getId().equals(UserContext.getUser())){
            throw new BusinessException("无权限!");
        }
        certificates.setStatu(CertificateStatusEnum.PENDING);
        certificatesService.save(certificates);
        return success();
    }
    @DeleteMapping("/deletUserCertificates/{id}")
    public ResponseResult deletUserCertificates(@PathVariable Integer id) throws BusinessException {
        Certificates certificates = certificatesService.getOne(new LambdaQueryWrapper<Certificates>().eq(Certificates::getId, id));
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, certificates.getUserId()));
        if(one == null){
            throw new BusinessException("用户不存在!");
        }
        if(!one.getId().equals(UserContext.getUser())){
            throw new BusinessException("无权限!");
        }
        certificatesService.removeById(id);
        return success();
    }
    @PutMapping("/updateWorker")
    public ResponseResult updateWorker(@RequestBody Worker worker ) throws BusinessException {
        Worker byId = workerService.getOne(new LambdaQueryWrapper<Worker>().eq(Worker::getUserId, worker.getUserId()));
        if(byId == null){
            throw new BusinessException("用户不存在!");
        }
        if(byId.getUserId().compareTo(UserContext.getUser())!=0){
            throw new BusinessException("无权限!");
        }
        workerService.update(worker,new LambdaQueryWrapper<Worker>().eq(Worker::getUserId, worker.getUserId()));
        UserVo login = getUserByTokenFromCookie();
        login.setWorker(BeanUtil.copyProperties(worker,WokerVo.class));
        redisComponent.saveUserToken(login, Constants.REDIS_KEY_TOKEN);
        return success();
    }
    @PutMapping("/updateEnterprise")
    public ResponseResult updateEnterprise(@RequestBody Enterprises enterprises ) throws BusinessException {
        Enterprises byId = enterprisesService.getOne(new LambdaQueryWrapper<Enterprises>().eq(Enterprises::getUserId,enterprises.getUserId()));
        if(byId == null){
            throw new BusinessException("用户不存在!");
        }
        if(byId.getUserId().compareTo(UserContext.getUser())!=0){
            throw new BusinessException("无权限!");
        }
        enterprisesService.update(enterprises,new LambdaQueryWrapper<Enterprises>().eq(Enterprises::getUserId, enterprises.getUserId()));
        UserVo login = getUserByTokenFromCookie();
        login.setEnterprise(BeanUtil.copyProperties(enterprises, EnterpriseVo.class));
        redisComponent.saveUserToken(login, Constants.REDIS_KEY_TOKEN);
        return success();
    }
}