package com.fhxf.domain.dto.job;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskApplicantDTO {
    private Integer jobId;
    private Integer workerId;
    private String username;
    private String avatar;
    private List<String> label;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime applyTime;
    private JobAcceptanceStatusEnum status;
    private String income;
    @JsonSetter
    public void setLabel(String label) {
        this.label = JSONUtil.toList(label, String.class);
    }
}