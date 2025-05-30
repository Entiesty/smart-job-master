package com.fhxf.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fhxf.annotation.Role;
import com.fhxf.domain.dto.job.EmployerTaskDTO;
import com.fhxf.domain.dto.job.TaskApplicantDTO;
import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import com.fhxf.domain.enums.JobStatusEnum;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.po.Enterprises;
import com.fhxf.domain.po.JobAcceptances;
import com.fhxf.domain.po.Jobs;
import com.fhxf.global.ResponseResult;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.service.JobsService;
import com.fhxf.service.NotificationService;
import com.fhxf.service.impl.EnterprisesServiceImpl;
import com.fhxf.service.impl.JobAcceptancesServiceImpl;
import com.fhxf.utils.NotificationUtils;
import com.fhxf.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Implement proper authentication and authorization (e.g., Spring Security)
// For now, assume employerUserId is obtained through a placeholder mechanism or passed directly.

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class EnterpriseController extends ABaseController {

    private final JobsService jobsService;
    private final JobAcceptancesServiceImpl jobAcceptancesService;
    private final EnterprisesServiceImpl enterprisesService;
    private final NotificationUtils notificationUtils;
    /**
     * 获取当前雇主发布的任务列表
     */
    @GetMapping("/tasks")
    public ResponseResult getMyTasks() {
        Long user = UserContext.getUser();
        List<Jobs> list = jobsService.list(new LambdaQueryWrapper<Jobs>().eq(Jobs::getUserId, user));
        if (list == null) {
            return fail("任务列表为空");
        }
        List<EmployerTaskDTO> tasks = BeanUtil.copyToList(list, EmployerTaskDTO.class);
        return success(tasks);
    }

    /**
     * 获取指定任务的申请人列表
     * // TODO: Replace placeholder employerId with actual authenticated user ID
     */
    @GetMapping("/tasks/{jobId}/applicants")
    public ResponseResult getTaskApplicants(@PathVariable Integer jobId) {
        Long userId = UserContext.getUser();
        List<TaskApplicantDTO> list = jobAcceptancesService.getApplicants(jobId, userId);
        return success(list);
    }


    @PutMapping("/applications/{acceptanceId}/{status}")
    public ResponseResult updateApplicationStatus(@PathVariable Integer acceptanceId, @PathVariable Integer status) {
        Long userId = UserContext.getUser();
        JobAcceptances byId;
        if (status == JobAcceptanceStatusEnum.WAITCONFIRMEDONE.getCode() || status == JobAcceptanceStatusEnum.CANCELLED.getCode()) {
            byId = jobAcceptancesService.getOne(new LambdaQueryWrapper<JobAcceptances>().eq(JobAcceptances::getJobId, acceptanceId).eq(JobAcceptances::getWorkerId, userId));
        } else {
            byId = jobAcceptancesService.getOne(new LambdaQueryWrapper<JobAcceptances>().eq(JobAcceptances::getJobId, acceptanceId).eq(JobAcceptances::getEnterpriseId, userId));
        }
        if (byId == null) {
            return fail("申请不存在");
        }
        JobAcceptanceStatusEnum jobAcceptanceStatusEnum = JobAcceptanceStatusEnum.of(status);

        switch (jobAcceptanceStatusEnum){
            case WAITCONFIRMEDONE:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.DOING)) {
                    return fail("状态错误");
                }
                if( byId.getWorkerId().compareTo(userId) != 0){
                    return fail("权限错误");
                }
                notificationUtils.sendSystemNotification(byId.getEnterpriseId(), "任务完成", "员工已经完成了任务，请及时确认");
                break;
            case CANCELLED:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.PENDING)){
                    return fail("状态错误");
                }
                notificationUtils.sendSystemNotification(byId.getEnterpriseId(), "任务取消", "员工已经取消了任务");
                break;
            case DOING:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.PENDING)){
                    return fail("状态错误");
                }
                notificationUtils.sendSystemNotification(byId.getWorkerId(), "任务开始", "雇主已经接受任务，请及时完成");
                break;
            case DONE:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.DOING) && !byId.getStatus().equals(JobAcceptanceStatusEnum.WAITCONFIRMEDONE)  ){
                    return fail("状态错误");
                }
                notificationUtils.sendSystemNotification(byId.getEnterpriseId().compareTo(userId)==0?byId.getWorkerId():userId, "任务完成", "已经完成了任务，请及时确认");
                break;
            case JOBOVER:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.DOING) ){
                    return fail("状态错误");
                }
                if( byId.getEnterpriseId().compareTo(userId) != 0){
                    return fail("权限错误");
                }
                notificationUtils.sendSystemNotification(byId.getWorkerId(), "任务完成", "任务已经结束，请及时确认");
            case REJECTED:
                if( !byId.getStatus().equals(JobAcceptanceStatusEnum.PENDING)){
                    return fail("状态错误");
                }
                notificationUtils.sendSystemNotification(byId.getWorkerId(), "任务拒绝", "雇主拒绝了任务，请及时处理");
                break;
            default:
                return fail("状态错误");
        }
        Jobs job = jobsService.getById(byId.getJobId());
        job.setHeadcount(jobAcceptanceStatusEnum.equals(JobAcceptanceStatusEnum.DOING )? job.getHeadcount() - 1 : job.getHeadcount());
        if (job.getHeadcount() == 0) {
            job.setStatus(JobStatusEnum.ENDED);
        }
        jobsService.updateById(job);
        byId.setStatus(jobAcceptanceStatusEnum);
        jobAcceptancesService.updateById(byId);

        return success();
    }

    @PostMapping("/postJob")
    @Role(UserRoleEnum.ENTERPRISE)
    public ResponseResult postJob(@RequestBody Jobs job) {
        Enterprises one = enterprisesService.getOne(new LambdaQueryWrapper<Enterprises>().eq(Enterprises::getUserId, UserContext.getUser()));
        // 设置企业名称
        job.setCompanyName(one.getCompanyName());
        // 设置状态为招聘中
        job.setStatus(JobStatusEnum.RECRUITING);
        boolean save = jobsService.save(job);
        if (!save) {
            throw new BusinessException("发布失败");
        }
        return success();
    }

    @PutMapping("/pay/{jobId}/{workId}")
    @Role(UserRoleEnum.ENTERPRISE)
    public ResponseResult pay(@PathVariable  Integer jobId, @PathVariable Integer workId) {
        JobAcceptances one = jobAcceptancesService.getOne(new LambdaQueryWrapper<JobAcceptances>().eq(JobAcceptances::getJobId, jobId).eq(JobAcceptances::getWorkerId, workId));
       if(one == null){
           throw new BusinessException("申请不存在");
       }
       if(one.getStatus() != JobAcceptanceStatusEnum.DONE){
           throw new BusinessException("申请状态错误");
       }
       if(one.getStatus() == JobAcceptanceStatusEnum.PAID){
           throw new BusinessException("申请已支付");
       }
       if(one.getEnterpriseId().compareTo(UserContext.getUser()) != 0){
           throw new BusinessException("无权限");
       }
        if( !one.getStatus().equals(JobAcceptanceStatusEnum.DONE)){
            return fail("状态错误");
        }
        notificationUtils.sendSystemNotification(one.getWorkerId(), "任务完成", "雇主已经支付，请及时确认");
       one.setStatus(JobAcceptanceStatusEnum.PAID);
       jobAcceptancesService.updateById(one);
       return success();
    }

}