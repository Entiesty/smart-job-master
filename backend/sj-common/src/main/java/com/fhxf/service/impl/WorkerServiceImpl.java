package com.fhxf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.dto.base.RegisterFormDTO;
import com.fhxf.domain.dto.register.WorkerRegisterFormDTO;
import com.fhxf.domain.po.Worker;
import com.fhxf.service.WorkerService;
import com.fhxf.mapper.WorkerMapper;
import com.fhxf.utils.UserContext;
import org.springframework.stereotype.Service;

/**
* @author fhxf111
* @description 针对表【worker】的数据库操作Service实现
* @createDate 2025-05-01 18:02:36
*/
@Service
public class WorkerServiceImpl extends ServiceImpl<WorkerMapper, Worker>
    implements WorkerService{

    public void createWorker(WorkerRegisterFormDTO registerFormDTO) {
        Worker worker = new Worker();
        worker.setLabel(registerFormDTO.getSkills().toString());
        worker.setUserId(registerFormDTO.getUserId());
        worker.setWorkDay(registerFormDTO.getWorkDay().toString());
        worker.setWorkTimeBegin(registerFormDTO.getWorkTimeBegin());
        worker.setWorkTimeEnd(registerFormDTO.getWorkTimeEnd());
        worker.setLocation(registerFormDTO.getLocation());
        worker.setRating(0.0);
        baseMapper.insert(worker);
    }
}




