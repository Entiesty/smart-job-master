package com.fhxf.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fhxf.domain.po.Certificates;
import com.fhxf.domain.po.Review;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WokerVo {
    private Integer id;
    private String exp;
    @JsonFormat(pattern = "HH:mm")
    private Date workTimeBegin;
    @JsonFormat(pattern = "HH:mm")
    private Date workTimeEnd;
    private List<String> workDay;
    private List<Certificates> certificates;
    private List<String> label;
    private Double rating;
    private String introduction;
    private List<ReviewVo> reviews;
    private String avatar;
    private String username;
    private String location;
    private Integer doneJobs;
}
