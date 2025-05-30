package com.fhxf.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.fhxf.domain.po.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fhxf.domain.vo.ReviewVo;
import com.fhxf.global.dto.MyPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author fhxf111
* @description 针对表【review】的数据库操作Mapper
* @createDate 2025-04-29 23:49:31
* @Entity com.fhxf.domain.po.Review
*/
public interface ReviewMapper extends BaseMapper<Review> {

    List<ReviewVo> listReviewVO(IPage<ReviewVo> page, @Param(Constants.WRAPPER) LambdaQueryWrapper<Review> ew);
}




