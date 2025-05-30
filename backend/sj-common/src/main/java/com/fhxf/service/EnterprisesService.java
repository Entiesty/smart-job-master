package com.fhxf.service;

import com.fhxf.domain.dto.register.EnterPriseRegisterFormDTO;
import com.fhxf.domain.po.Enterprises;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author fhxf111
* @description 针对表【enterprises】的数据库操作Service
* @createDate 2025-04-30 23:56:32
*/
public interface EnterprisesService extends IService<Enterprises> {

    void createEnterprise(EnterPriseRegisterFormDTO registerFormDTO);
}
