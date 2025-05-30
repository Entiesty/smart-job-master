package com.fhxf.mapper;

import com.fhxf.domain.dto.job.TaskApplicantDTO;
import com.fhxf.domain.po.JobAcceptances;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author fhxf111
* @description 针对表【job_acceptances(零工接受的任务)】的数据库操作Mapper
* @createDate 2025-05-02 18:03:08
* @Entity com.fhxf.domain.po.JobAcceptances
*/
public interface JobAcceptancesMapper extends BaseMapper<JobAcceptances> {

    List<TaskApplicantDTO> getApplicants(Integer jobId, Long userId);
}




