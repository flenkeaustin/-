package com.xiaolian.amigo.data.network.model.dto.request;

import lombok.Data;

/**
 * 登录
 * <p>
 * Created by zcd on 9/20/17.
 */
@Data
public class LoginReqDTO {
    private String mobile;
    private String password;
}