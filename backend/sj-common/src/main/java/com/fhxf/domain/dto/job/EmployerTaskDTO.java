package com.fhxf.domain.dto.job;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fhxf.domain.enums.JobStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EmployerTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private JobStatusEnum status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;
    private Integer headCount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
}