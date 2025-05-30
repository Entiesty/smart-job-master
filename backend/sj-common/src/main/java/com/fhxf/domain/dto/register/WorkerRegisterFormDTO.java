package com.fhxf.domain.dto.register;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fhxf.domain.dto.base.RegisterFormDTO;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkerRegisterFormDTO extends RegisterFormDTO {
    private List<String> skills;
    private String workExperience;
    private List<String> workDay;
    private String workTimeBegin;
    private String workTimeEnd;
    private String location;
}
