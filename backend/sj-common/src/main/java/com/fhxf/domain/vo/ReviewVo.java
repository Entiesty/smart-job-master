package com.fhxf.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewVo {
    private Integer id;
    private String content;
    private Integer rating;
    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    private LocalDateTime createdAt;
    private UserVo fromUser;
}
