package com.fhxf.domain.dto.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplyDTO {
    private Integer id;
    private Integer enterpriseId;
    private Integer days;
    private String message;
    private Double income;
}
