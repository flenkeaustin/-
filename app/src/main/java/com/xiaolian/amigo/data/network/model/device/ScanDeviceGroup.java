package com.xiaolian.amigo.data.network.model.device;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by caidong on 2017/10/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanDeviceGroup {
    /**
     * 是否已收藏
     */
    private Boolean favor;

    private String location;

    private Long residenceId;

    private Map<String, ScanDevice> water;
}
