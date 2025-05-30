package com.fhxf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.dto.register.EnterPriseRegisterFormDTO;
import com.fhxf.domain.po.Enterprises;
import com.fhxf.service.EnterprisesService;
import com.fhxf.mapper.EnterprisesMapper;
import org.springframework.stereotype.Service;

/**
* @author fhxf111
* @description 针对表【enterprises】的数据库操作Service实现
* @createDate 2025-04-30 23:56:32
*/
@Service
public class EnterprisesServiceImpl extends ServiceImpl<EnterprisesMapper, Enterprises>
    implements EnterprisesService{

    @Override
    public void createEnterprise(EnterPriseRegisterFormDTO registerFormDTO) {
        Enterprises enterprises = BeanUtil.copyProperties(registerFormDTO, Enterprises.class,  "id");
        baseMapper.insert(enterprises);
    }
}




