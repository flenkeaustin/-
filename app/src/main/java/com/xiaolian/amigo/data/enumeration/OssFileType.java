package com.xiaolian.amigo.data.enumeration;

/**
 * oss type
 * <p>
 * Created by zcd on 17/11/13.
 */

public enum OssFileType {
    /**
     * 用户头像
     */
    AVATAR(1, "avatar"),
    /**
     * 报修
     */
    REPAIR(2, "repair"),
    /**
     * 投诉
     */
    COMPLAINT(3, "complaint"),
    /**
     * 反馈
     */
    FEEDBACK(4, "feedback"),
    /**
     * 失物
     */
    LOST(5, "lost"),
    /**
     * 招领
     */
    FOUND(6, "found");

    OssFileType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    int type;
    String desc;

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}