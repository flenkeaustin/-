package com.xiaolian.amigo.data.vo;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * Created by zcd on 9/18/17.
 */
@Data
public class LostAndFound {

    private Long createTime;
    private String description;
    private Long id;
    private String itemName;
    private String location;
    private Long lostTime;
    private String mobile;
    private Long schoolId;
    private String schoolName;
    private String title;
    private Integer type;
    private String user;
    private Long userId;
    private List<String> images;

}