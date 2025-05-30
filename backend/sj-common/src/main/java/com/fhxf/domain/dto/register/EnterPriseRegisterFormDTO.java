package com.fhxf.domain.dto.register;

import com.fhxf.domain.dto.base.RegisterFormDTO;
import com.fhxf.domain.enums.EnterpriseCompanyTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel(description = "企业注册表单实体")
public class EnterPriseRegisterFormDTO extends RegisterFormDTO {
    @ApiModelProperty(value = "公司名称", required = true)
    @NotNull(message = "公司名称不能为空")
    private String companyName;
    @ApiModelProperty(value = "公司信用代码", required = true)
    @NotNull(message = "公司信用代码不能为空")
    private String creditCode;
    @ApiModelProperty(value = "营业执照", required = true)
    @NotNull(message = "营业执照不能为空")
    private String businessLicense;
    @ApiModelProperty(value = "公司类型", required = true)
    @NotNull(message = "公司类型不能为空")
    private EnterpriseCompanyTypeEnum companyType;
    @ApiModelProperty(value = "联系人姓名", required = true)
    @NotNull(message = "联系人姓名不能为空")
    private String contactName;
    @ApiModelProperty(value = "公司地址", required = true)
    @NotNull(message = "公司地址不能为空")
    private String address;

}
