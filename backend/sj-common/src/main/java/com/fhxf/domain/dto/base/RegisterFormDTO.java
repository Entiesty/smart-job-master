package com.fhxf.domain.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel(description = "注册表单实体")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterFormDTO extends VerifyCaptchaDTO {
    @ApiModelProperty(value = "用户Id", required = false)
    private Long userId;
    @ApiModelProperty(value = "用户名", required = true)
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String password;
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String confirmPassword;
    @NotNull(message = "role不能为空")
    @ApiModelProperty(value = "role", required = true)
    private Integer role;
    @ApiModelProperty(value = "联系人电话", required = true)
    @NotNull(message = "联系人电话不能为空")
    private String contactPhone;
    @ApiModelProperty(value = "联系人邮箱", required = true)
    @NotNull(message = "联系人邮箱不能为空")
    private String contactEmail;
}
