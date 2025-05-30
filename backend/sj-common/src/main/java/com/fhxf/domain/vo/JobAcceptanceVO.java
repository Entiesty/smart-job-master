package com.fhxf.domain.vo;

import com.fhxf.domain.po.JobAcceptances;
import com.fhxf.domain.po.Jobs;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@Accessors(chain = true)
public class JobAcceptanceVO extends JobAcceptances  {
    private String companyName;
    private String title;
    private String description;
    private String location;
    private LocalDateTime endDate;
    private String contractFilePath;
    private String otherFilePath;
}
