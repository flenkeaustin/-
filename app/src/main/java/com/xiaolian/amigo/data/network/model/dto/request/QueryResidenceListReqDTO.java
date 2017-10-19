package com.xiaolian.amigo.data.network.model.dto.request;

import lombok.Data;

/**
 * 获取建筑列表请求DTO
 * @author zcd
 *
 * residenceLevel 1幢 2楼层 3宿舍 具体位置 buildingType 1宿舍楼 parentId上一层事物Id
 */
@Data
public class QueryResidenceListReqDTO {
    Integer buildingType;
    Integer page;
    Integer size;
    Long parentId;
    Integer residenceLevel;
    Long schoolId;
    Boolean existDevice;
}
