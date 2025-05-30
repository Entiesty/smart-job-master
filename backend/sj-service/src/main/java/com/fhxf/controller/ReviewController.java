package com.fhxf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fhxf.domain.constans.Constants;
import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import com.fhxf.domain.po.JobAcceptances;
import com.fhxf.domain.po.Review;
import com.fhxf.domain.po.Worker;
import com.fhxf.domain.vo.ReviewVo;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.global.ResponseResult;
import com.fhxf.global.dto.MyPage;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.service.ReviewService;
import com.fhxf.service.impl.JobAcceptancesServiceImpl;
import com.fhxf.service.impl.WorkerServiceImpl;
import com.fhxf.utils.NotificationUtils;
import com.fhxf.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController extends ABaseController {

    private final ReviewService reviewService;
    private final JobAcceptancesServiceImpl jobAcceptancesService;
    private final WorkerServiceImpl workerService;
    private final NotificationUtils notificationUtils;


    @PostMapping
    public ResponseResult createReview(@RequestBody Review review) {
        JobAcceptances one = jobAcceptancesService.getOne(new LambdaQueryWrapper<JobAcceptances>()
                .eq(JobAcceptances::getJobId, review.getJobId()));
        if(one.getStatus()!= JobAcceptanceStatusEnum.PAID){
            throw new BusinessException("任务状态错误");
        }
        Long enterpriseId = one.getEnterpriseId();
        Long workerId = one.getWorkerId();
        if(workerId.compareTo(UserContext.getUser()) != 0 && enterpriseId.compareTo(UserContext.getUser()) != 0){
            throw new BusinessException("无权限");
        }
        if((enterpriseId.compareTo(UserContext.getUser()) == 0 && workerId.compareTo(review.getToUserId()) == 0)
                ||  (workerId.compareTo(UserContext.getUser()) == 0 && enterpriseId.compareTo(review.getToUserId()) == 0)){
            review.setFromUserId(UserContext.getUser());
        }
        notificationUtils.sendSystemNotification(review.getToUserId(),"评价任务","收到评价");
        reviewService.save(review);
        one.setStatus(JobAcceptanceStatusEnum.JOBOVER);
        jobAcceptancesService.updateById(one);
        //评价
        List<Review> list = reviewService.list(new LambdaQueryWrapper<Review>().eq(Review::getToUserId,review.getToUserId()));
        if(!list.isEmpty()){
            Worker byId = workerService.getOne(new LambdaQueryWrapper<Worker>().eq(Worker::getUserId,review.getToUserId()));
            if(byId!=null){
                double sum = list.stream().mapToDouble(Review::getRating).sum();
                byId.setRating(sum/list.size());
                workerService.updateById(byId);
            }
        }
        return success();
    }

    @PostMapping("/review")
    public ResponseResult getReviews(@RequestBody MyPage page ) {
        Page<Review> page1 = new Page<>();
        if(page!=null){
            page1.setCurrent(page.getCurrent());
            page1.setSize(page.getSize());
        }
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        Long user = UserContext.getUser();

        if (page!=null){
            if(!StrUtil.isEmpty(page.getRating()) ){
                String[] split = page.getRating().split("-");
                queryWrapper.ge(Review::getRating,split[0]).le(Review::getRating,split[1]);
            }
            if(!StrUtil.isEmpty(page.getType())){
                queryWrapper.func(i -> {
                    if(page.getType().equals("me")){
                        i.eq(Review::getToUserId, user);
                    }
                    else if(page.getType().equals("other")){
                        i.ne(Review::getToUserId, user);
                    }else {
                        i.eq(Review::getToUserId, user).or().eq(Review::getFromUserId, user);
                    }
                });
            }
            if (!StrUtil.isEmpty(page.getTime())){
                queryWrapper.func(i -> {
                   switch (page.getTime()){
                       case "week":
                           i.gt(Review::getCreatedAt, LocalDateTime.now().minusDays(7));
                           break;
                       case "month":
                           i.gt(Review::getCreatedAt, LocalDateTime.now().minusMonths(1));
                           break;
                       case "quarter":
                           i.gt(Review::getCreatedAt, LocalDateTime.now().minusMonths(3));
                           break;
                   }
                });
            }
        }
        List<ReviewVo> reviews = reviewService.listReviewVO( page,queryWrapper);
        return success(reviews);
    }
}