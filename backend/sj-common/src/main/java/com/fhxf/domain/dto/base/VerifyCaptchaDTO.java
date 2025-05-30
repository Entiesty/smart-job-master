package com.fhxf.domain.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel(description = "验证码实体")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyCaptchaDTO {
    @ApiModelProperty(value = "验证码key", required = true)
    @NotEmpty
    private String captcha;
    @ApiModelProperty(value = "验证码", required = true)
    @NotEmpty
    private String captchaKey;

}