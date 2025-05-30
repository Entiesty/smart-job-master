package com.fhxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.po.Review;
import com.fhxf.domain.vo.ReviewVo;
import com.fhxf.global.dto.MyPage;
import com.fhxf.service.ReviewService;
import com.fhxf.mapper.ReviewMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author fhxf111
* @description 针对表【review】的数据库操作Service实现
* @createDate 2025-04-29 23:49:31
*/
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
    implements ReviewService{

    @Override
    public List<ReviewVo> listReviewVO(IPage<ReviewVo> page, LambdaQueryWrapper<Review> ew) {
        return baseMapper.listReviewVO(page,ew);
    }
}




