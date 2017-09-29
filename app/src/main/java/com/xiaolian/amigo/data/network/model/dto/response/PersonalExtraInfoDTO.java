package com.xiaolian.amigo.data.network.model.dto.response;

import com.xiaolian.amigo.data.network.model.notify.Notify;

import lombok.Data;

/**
 * 用户个人中心额外信息
 * <p>
 * Created by zcd on 9/22/17.
 */
@Data
public class PersonalExtraInfoDTO {
    private Integer balance;
    private Integer bonusAmount;
    private Integer notifyAmount;
    private Integer prepay;
    private Long repair;
    private Notify urgentNotify;
}