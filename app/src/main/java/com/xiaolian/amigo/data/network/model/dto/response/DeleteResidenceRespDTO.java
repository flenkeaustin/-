package com.xiaolian.amigo.data.network.model.dto.response;

import lombok.Data;

/**
 * 宿舍删除返回的默认宿舍id
 * <p>
 * Created by zcd on 17/11/2.
 */
@Data
public class DeleteResidenceRespDTO {
    private Long residenceId;
    private String residenceName;
}
