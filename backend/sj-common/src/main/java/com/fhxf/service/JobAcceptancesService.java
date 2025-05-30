package com.fhxf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fhxf.domain.dto.job.TaskApplicantDTO;
import com.fhxf.domain.po.JobAcceptances;

import java.util.List;

public interface JobAcceptancesService extends IService<JobAcceptances> {
    List<TaskApplicantDTO> getApplicants(Integer jobId, Long userId);

}