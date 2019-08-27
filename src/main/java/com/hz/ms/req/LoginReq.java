package com.hz.ms.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录请求参数
 */
@Data
public class LoginReq {

    @NotNull(message ="手机不能为空")
//    @Ism
    private String mobile;

    @NotNull(message ="密码不能为空")
    @Length(min = 6, message = "密码长度需要在7个字以内")
    private String password;
}
