package com.xiaolian.amigo.data.network.model.trade;

import lombok.Data;

/**
 * Created by caidong on 2017/10/9.
 */
@Data
public class ConnectCommandRespDTO {

    // 连接设备指令内容
    private String connectCmd;
    String macAddress;
    String deviceToken;
}