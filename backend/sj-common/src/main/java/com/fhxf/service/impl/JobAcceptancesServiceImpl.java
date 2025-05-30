package com.fhxf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.dto.job.TaskApplicantDTO;
import com.fhxf.domain.po.JobAcceptances;
import com.fhxf.mapper.JobAcceptancesMapper; // Assuming JobAcceptancesMapper exists
import com.fhxf.mapper.JobsMapper; // Assuming JobsMapper exists
import com.fhxf.mapper.UserMapper;
import com.fhxf.service.JobAcceptancesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAcceptancesServiceImpl extends ServiceImpl<JobAcceptancesMapper, JobAcceptances> implements JobAcceptancesService {

    private final UserMapper userMapper; // To get applicant details
    private final JobsMapper jobsMapper;   // To verify job ownership for authorization


    @Override
    public List<TaskApplicantDTO> getApplicants(Integer jobId, Long userId) {
        return baseMapper.getApplicants(jobId,  userId);
    }
}