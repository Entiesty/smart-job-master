package com.fhxf.controller;

import cn.hutool.Hutool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fhxf.annotation.Role;
import com.fhxf.domain.dto.job.JobApplyDTO;
import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import com.fhxf.domain.enums.JobStatusEnum;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.po.*;

import com.fhxf.domain.vo.JobAcceptanceVO;
import com.fhxf.domain.vo.ReviewVo;
import com.fhxf.domain.vo.WokerVo;
import com.fhxf.global.ResponseResult;
import com.fhxf.global.dto.MyPage;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.service.impl.*;
import com.fhxf.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class WorkerController extends ABaseController{

    private  final JobsServiceImpl jobsService;
    private  final JobAcceptancesServiceImpl jobAcceptancesService;
    private  final WorkerServiceImpl workerService;
    private  final ReviewServiceImpl reviewService;
    private  final UserServiceImpl userService;
    @PostMapping("/jobs")
    public ResponseResult getJobs(@RequestBody MyPage page) {
        Page<Jobs> page1 = new Page<>();
        if(page!=null){
            page1.setCurrent(page.getCurrent());
            page1.setSize(page.getSize());
        }
        LambdaQueryWrapper<Jobs> jobsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<JobAcceptances> list = jobAcceptancesService.list(new LambdaQueryWrapper<JobAcceptances>()
                .eq(JobAcceptances::getWorkerId, UserContext.getUser()));
        List<Integer> jobIds = new ArrayList<>();
        if (list != null && list.size() > 0){
            jobIds = list.stream().map(JobAcceptances::getJobId).toList();
        }
        jobsLambdaQueryWrapper.ne(Jobs::getStatus, JobStatusEnum.ENDED).notIn(!jobIds.isEmpty(),Jobs::getId, jobIds);
        return success(jobsService.page(page1,jobsLambdaQueryWrapper).getRecords());
    }
    @GetMapping("/jobs/{id}")
    public ResponseResult getJobById(@PathVariable Integer id) {
        return success(jobsService.getById(id));
    }
    @PostMapping("/apply")
    public ResponseResult apply(@RequestBody JobApplyDTO jobApplyDTO) {
        JobAcceptances one = jobAcceptancesService.getOne(new LambdaQueryWrapper<JobAcceptances>().eq(JobAcceptances::getWorkerId, UserContext.getUser()).eq(JobAcceptances::getJobId, jobApplyDTO.getId()));
        if (one != null) {
            throw new BusinessException("您已经申请过该任务");
        }
        JobAcceptances jobAcceptances = BeanUtil.copyProperties(jobApplyDTO, JobAcceptances.class,"id");
        jobAcceptances.setStatus(JobAcceptanceStatusEnum.PENDING);
        jobAcceptances.setWorkerId(UserContext.getUser());
        jobAcceptances.setJobId(jobApplyDTO.getId());
        jobAcceptancesService.save(jobAcceptances);
        return success("申请成功");
    }

    @GetMapping("/getJobAcceptances")
    public ResponseResult getJobAcceptances() {
        List<JobAcceptances> list = jobAcceptancesService.list(new LambdaQueryWrapper<JobAcceptances>().eq(JobAcceptances::getWorkerId, UserContext.getUser()));
        List<JobAcceptanceVO> jobAcceptanceVOS = BeanUtil.copyToList(list, JobAcceptanceVO.class);
        jobAcceptanceVOS.forEach(jobAcceptanceVO -> {
            Jobs byId = jobsService.getById(jobAcceptanceVO.getJobId());
            jobAcceptanceVO.setCompanyName(byId.getCompanyName())
                    .setTitle(byId.getTitle())
                    .setDescription(byId.getDescription())
                    .setLocation(byId.getLocation())
                    .setEndDate(byId.getEndDate())
                    .setContractFilePath(byId.getContractFilePath())
                    .setOtherFilePath(byId.getOtherFilePath());
        });
        return success(jobAcceptanceVOS);
    }
    @GetMapping("/getSkillLabel")
    public ResponseResult getSkillLabel() throws BusinessException {
        List<Jobs> list = jobsService.list(new LambdaQueryWrapper<Jobs>().eq(Jobs::getStatus, JobStatusEnum.ONGOING));
        List<String> labels = new ArrayList<>();
         list.stream().forEach(
                 job -> {
                     List<String> skills = job.getSkills();
                     labels.addAll(skills);
                 }
         );
        return success(labels.stream().distinct().toList());
    }
    @GetMapping("/getWorkerSkillLabel")
    public ResponseResult getWorkerSkillLabel() throws BusinessException {
        List<Worker> list = workerService.list();
        List<String> labels = new ArrayList<>();
        list.stream().forEach(
                job -> {
                    List<String> skills = job.getJsonLabel();
                    labels.addAll(skills);
                }
        );
        return success(labels.stream().distinct().toList());
    }
    @GetMapping("/getLocations")
    public ResponseResult getLocations() throws BusinessException {
        return success( jobsService.list().stream().map(Jobs::getLocation).distinct().toList());
    }
    @GetMapping("/getWorkerLocations")
    public ResponseResult getWorkerLocations() throws BusinessException {
        return success( workerService.list().stream().map(Worker::getLocation).distinct().toList());
    }
    @PostMapping("/getWorkers")
    public ResponseResult getWorkers(@RequestBody MyPage page) throws BusinessException {
        Page<Worker> page1 = new Page<>();
        if(page!=null){
            page1.setCurrent(page.getCurrent());
            page1.setSize(page.getSize());
        }
        LambdaQueryWrapper<Worker> queryWrapper = new LambdaQueryWrapper<>();
        if(!StrUtil.isEmpty(page.getRating()) ){
            String[] split = page.getRating().split("-");
            queryWrapper.ge(Worker::getRating,split[0]).le(Worker::getRating,split[1]);
        }
        if(!StrUtil.isEmpty(page.getLocation())){
            queryWrapper.eq(Worker::getLocation,page.getLocation());
        }
        if(!StrUtil.isEmpty(page.getSkill())){
            queryWrapper.like(Worker::getLabel,page.getSkill());
        }
        List<Worker> records = workerService.page(page1, queryWrapper).getRecords();
         List<WokerVo> list = new ArrayList<>();
        records.forEach(worker -> {
            User byId = userService.getById(worker.getUserId());
            List<ReviewVo> reviews = reviewService.listReviewVO(null,new LambdaQueryWrapper<Review>().eq(Review::getToUserId, worker.getUserId()));
            WokerVo wokerVo = BeanUtil.copyProperties(worker, WokerVo.class,"label","workDay");
            wokerVo.setLabel(worker.getJsonLabel());
            wokerVo.setWorkDay(worker.getJsonWorkDay());
            wokerVo.setLocation(byId.getLocation());
            wokerVo.setIntroduction(byId.getIntroduction());
            wokerVo.setAvatar(byId.getAvatar());
            wokerVo.setUsername(byId.getUsername());
            wokerVo.setReviews(reviews);
            wokerVo.setDoneJobs(worker.getDoneJobs());
            if(worker.getRating()==null){
                worker.setRating(reviews.stream().mapToDouble(ReviewVo::getRating).average().orElse(0));
                workerService.update(worker,new LambdaQueryWrapper<Worker>().eq(Worker::getId, worker.getId()));
            }
            wokerVo.setRating(worker.getRating());
            list.add(wokerVo);
        });
        return success(list);
    }


}