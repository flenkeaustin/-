package com.xiaolian.amigo.data.prefs;

import com.xiaolian.amigo.data.network.model.user.User;

/**
 * SharedPreference帮助接口
 * @author zcd
 */

public interface ISharedPreferencesHelp {
    // 储存蓝牙地址和编号的映射关系
    String getToken();

    void setToken(String token);

    User getUserInfo();

    void setUserInfo(User user);

    boolean isShowUrgencyNotify();

    void setShowUrgencyNotify(boolean isShow);

    void setBonusAmount(int amount);

    int getBonusAmount();

    void setBalance(String balance);

    String getBalance();

    void logout();

    // current http device token
    void setCurrentDeviceToken(String deviceToken);

    String getCurrentDeviceToken();

    // http device token
    void setDeviceToken(String macAddress, String deviceToken);

    String getDeviceToken(String macAddress);

    // 握手指令
    void setConnectCmd(String macAddress, String connectCmd);

    String getConnectCmd(String macAddress);

    // 关阀指令
    void setCloseCmd(String macAddress, String closeCmd);

    String getCloseCmd(String macAddress);

    void setDeviceNoAndMacAddress(String deviceNo, String macAddress);

    String getMacAddressByDeviceNo(String deviceNo);

    // 上次连接时间
    void setLastConnectTime(Long lastConnectTime);

    Long getLastConnectTime();

    Long getLastUpdateRemindTime();

    void setLastUpdateRemindTime();

    void setLastWithdrawId(Long id);

    Long getLastWithdrawId();

    void setLastWithdrawName(String name);

    String getLastWithdrawName();

    String getLastRechargeAmount();

    void setLastRechargeAmount(String amount);
}
