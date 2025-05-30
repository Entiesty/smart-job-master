package com.fhxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.po.Jobs;
import com.fhxf.domain.po.Enterprises;
import com.fhxf.mapper.JobsMapper; // Assuming JobsMapper exists
import com.fhxf.mapper.EnterprisesMapper; // Assuming EnterprisesMapper exists
import com.fhxf.mapper.JobAcceptancesMapper; // Assuming JobAcceptancesMapper exists
import com.fhxf.service.JobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobsServiceImpl extends ServiceImpl<JobsMapper, Jobs> implements JobsService {

    private final EnterprisesMapper enterprisesMapper; // Inject if needed for employer info
    private final JobAcceptancesMapper jobAcceptancesMapper; // Inject for applicant count


}