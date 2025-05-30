package com.fhxf.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fhxf.domain.po.Review;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fhxf.domain.vo.ReviewVo;
import com.fhxf.global.dto.MyPage;

import java.util.List;

/**
* @author fhxf111
* @description 针对表【review】的数据库操作Service
* @createDate 2025-04-29 23:49:31
*/
public interface ReviewService extends IService<Review> {

    List<ReviewVo> listReviewVO(IPage<ReviewVo> page, LambdaQueryWrapper<Review> eq);
}
