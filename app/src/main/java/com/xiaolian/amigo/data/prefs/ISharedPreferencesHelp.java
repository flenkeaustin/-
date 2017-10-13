package com.xiaolian.amigo.data.prefs;

import com.xiaolian.amigo.data.network.model.user.User;

/**
 * SharedPreference帮助接口
 * @author zcd
 */

public interface ISharedPreferencesHelp {
    String getToken();

    void setToken(String token);

    User getUserInfo();

    void setUserInfo(User user);

    boolean isShowUrgencyNotify();

    void setShowUrgencyNotify(boolean isShow);

    void setBonusAmount(int amount);

    int getBonusAmount();

    void logout();

    void setDeviceToken(String deviceToken);

    String getDeviceToken();

    // 握手指令
    void setConnectCmd(String connectCmd);

    String getConnectCmd();

    // 关阀指令
    void setCloseCmd(String closeCmd);

    String getCloseCmd();
}
