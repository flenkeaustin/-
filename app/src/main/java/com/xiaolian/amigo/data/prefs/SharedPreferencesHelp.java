package com.xiaolian.amigo.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import com.xiaolian.amigo.data.network.model.user.User;
import com.xiaolian.amigo.di.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * SharedPreferencesHelp实现类
 *
 * @author zcd
 */
public class SharedPreferencesHelp implements ISharedPreferencesHelp {
    private static final String PREF_FILE_NAME = "amigo";
    private static final String PREF_KEY_TOKEN = "PREF_KEY_TOKEN";
    private static final String PREF_DEVICE_TOKEN_PREFIX = "PREF_DEVICE_TOKEN_";
    private static final String PREF_CURRENT_DEVICE_TOKEN = "PREF_CURRENT_DEVICE_TOKEN";
    private static final String PREF_KEY_RESIDENCEID = "PREF_KEY_RESIDENCEID";
    private static final String PREF_KEY_SCHOOLID = "PREF_KEY_SCHOOLID";
    private static final String PREF_KEY_SCHOOLNAME = "PREF_KEY_SCHOOLNAME";
    private static final String PREF_KEY_NICKNAME = "PREF_KEY_NICKNAME";
    private static final String PREF_KEY_MOBILE = "PREF_KEY_MOBILE";
    private static final String PREF_KEY_PICTURE_URL = "PREF_KEY_PICTURE_URL";
    private static final String PREF_CMD_CONNECT_PREFIX = "PREF_CMD_CONNECT_";
    private static final String PREF_CMD_CLOSE_PREFIX = "PREF_CMD_CLOSE_";
    private static final String PREF_LAST_CONNECT_TIME = "PREF_LAST_CONNECT_TIME";
    private static final String PREF_KEY_BALANCE = "PREF_KEY_BALANCE";
    private static final String PREF_KEY_BONUS = "PREF_KEY_BONUS";
    private static final String PREF_BLUETOOTH_MAC_ADDRESS_PREFIX = "PREF_BLUETOOTH_MAC_ADDRESS_PREFIX";
    private static final String PREF_LAST_UPDATE_REMIND_TIME = "PREF_LAST_UPDATE_REMIND_TIME";
    private static final String PREF_LAST_VIEW_REPAIR = "PREF_LAST_VIEW_REPAIR";
    private static final String PREF_LAST_WITHDRAW_ID = "PREF_LAST_WITHDRAW_ID";
    private static final String PREF_LAST_WITHDRAW_NAME = "PREF_LAST_WITHDRAW_NAME";
    private static final String PREF_LAST_RECHARGE_AMOUNT = "PREF_LAST_RECHARGE_AMOUNT";

    private String tokenHolder;
    private String deviceTokenHolder;
    private User userHolder;

    private boolean isShowUrgencyNotify = true;
    private int bonusAmount = 0;
    private String balance = "";

    private final SharedPreferences mSharedPreferences;

    @Inject
    public SharedPreferencesHelp(@ApplicationContext Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String getToken() {
        if (tokenHolder != null) {
            return tokenHolder;
        }
        tokenHolder = mSharedPreferences.getString(PREF_KEY_TOKEN, null);
        return tokenHolder;
    }

    @Override
    public void setToken(String token) {
        tokenHolder = token;
        mSharedPreferences.edit().putString(PREF_KEY_TOKEN, token).apply();
    }

    @Override
    public void setCurrentDeviceToken(String deviceToken) {
        mSharedPreferences.edit().putString(PREF_CURRENT_DEVICE_TOKEN, deviceToken).apply();
    }

    @Override
    public String getCurrentDeviceToken() {
        return mSharedPreferences.getString(PREF_CURRENT_DEVICE_TOKEN, null);
    }

    @Override
    public void setDeviceToken(String macAddress, String deviceToken) {
        mSharedPreferences.edit().putString(PREF_DEVICE_TOKEN_PREFIX + macAddress, deviceToken).apply();
    }

    @Override
    public String getDeviceToken(String macAddress) {
        return mSharedPreferences.getString(PREF_DEVICE_TOKEN_PREFIX + macAddress, null);
    }

    @Override
    public User getUserInfo() {
        if (!isUserHolderEmpty()) {
            return userHolder;
        }
        if (userHolder == null) {
            userHolder = new User();
        }
        userHolder.setResidenceId(mSharedPreferences.getLong(PREF_KEY_RESIDENCEID, -1));
        userHolder.setSchoolId(mSharedPreferences.getLong(PREF_KEY_SCHOOLID, -1));
        userHolder.setSchoolName(mSharedPreferences.getString(PREF_KEY_SCHOOLNAME, null));
        userHolder.setNickName(mSharedPreferences.getString(PREF_KEY_NICKNAME, null));
        userHolder.setMobile(mSharedPreferences.getString(PREF_KEY_MOBILE, null));
        userHolder.setPictureUrl(mSharedPreferences.getString(PREF_KEY_PICTURE_URL, null));
        return userHolder;
    }

    @Override
    public void setUserInfo(User user) {
        userHolder = user;
        if (null != user.getResidenceId()) {
            mSharedPreferences.edit().putLong(PREF_KEY_RESIDENCEID, user.getResidenceId()).apply();
        }
        mSharedPreferences.edit().putLong(PREF_KEY_SCHOOLID, user.getSchoolId()).apply();
        mSharedPreferences.edit().putString(PREF_KEY_SCHOOLNAME, user.getSchoolName()).apply();
        mSharedPreferences.edit().putString(PREF_KEY_NICKNAME, user.getNickName()).apply();
        mSharedPreferences.edit().putString(PREF_KEY_MOBILE, user.getMobile()).apply();
        if (null != user.getPictureUrl()) {
            mSharedPreferences.edit().putString(PREF_KEY_PICTURE_URL, user.getPictureUrl()).apply();
        }
    }

    @Override
    public boolean isShowUrgencyNotify() {
        return this.isShowUrgencyNotify;
    }

    @Override
    public void setShowUrgencyNotify(boolean isShow) {
        this.isShowUrgencyNotify = isShow;
    }

    @Override
    public void setBonusAmount(int amount) {
        this.bonusAmount = amount;
        mSharedPreferences.edit().putInt(PREF_KEY_BONUS, amount).apply();
    }

    @Override
    public int getBonusAmount() {
        if (bonusAmount == 0) {
            bonusAmount = mSharedPreferences.getInt(PREF_KEY_BONUS, 0);
        }
        return this.bonusAmount;
    }

    @Override
    public void setBalance(String balance) {
        this.balance = balance;
        mSharedPreferences.edit().putString(PREF_KEY_BALANCE, balance).apply();
    }

    @Override
    public String getBalance() {
        if (TextUtils.isEmpty(balance)) {
            balance = mSharedPreferences.getString(PREF_KEY_BALANCE, "");
        }
        return this.balance;
    }

    @Override
    public void setLastConnectTime(Long lastConnectTime) {
        mSharedPreferences.edit().putLong(PREF_LAST_CONNECT_TIME, System.currentTimeMillis()).apply();
    }

    @Override
    public Long getLastConnectTime() {
        return mSharedPreferences.getLong(PREF_LAST_CONNECT_TIME, 0L);
    }

    @Override
    public Long getLastUpdateRemindTime() {
        return mSharedPreferences.getLong(PREF_LAST_UPDATE_REMIND_TIME, 0L);
    }

    @Override
    public void setLastUpdateRemindTime() {
        mSharedPreferences.edit().putLong(PREF_LAST_UPDATE_REMIND_TIME, System.currentTimeMillis()).apply();
    }

    @Override
    public void setLastWithdrawId(Long id) {
        mSharedPreferences.edit().putLong(PREF_LAST_WITHDRAW_ID, id).apply();
    }

    @Override
    public Long getLastWithdrawId() {
        return mSharedPreferences.getLong(PREF_LAST_WITHDRAW_ID, -1);
    }

    @Override
    public void setLastWithdrawName(String name) {
        mSharedPreferences.edit().putString(PREF_LAST_WITHDRAW_NAME, name).apply();
    }

    @Override
    public String getLastWithdrawName() {
        return mSharedPreferences.getString(PREF_LAST_WITHDRAW_NAME, "");
    }

    @Override
    public String getLastRechargeAmount() {
        return mSharedPreferences.getString(PREF_LAST_RECHARGE_AMOUNT, "");
    }

    @Override
    public void setLastRechargeAmount(String amount) {
        mSharedPreferences.edit().putString(PREF_LAST_RECHARGE_AMOUNT, amount).apply();
    }

    @Override
    public void logout() {
        mSharedPreferences.edit().clear().apply();
        tokenHolder = null;
        userHolder = null;
        bonusAmount = 0;
        balance = "";
        isShowUrgencyNotify = true;
    }


    boolean isUserHolderEmpty() {
        if (userHolder != null) {
            if (userHolder.getNickName() != null
                    && userHolder.getSchoolName() != null
                    && userHolder.getMobile() != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setConnectCmd(String macAddress, String connectCmd) {
        mSharedPreferences.edit().putString(PREF_CMD_CONNECT_PREFIX + macAddress, connectCmd).apply();
    }

    @Override
    public String getConnectCmd(String macAddress) {
        return mSharedPreferences.getString(PREF_CMD_CONNECT_PREFIX + macAddress, null);
    }

    @Override
    public void setCloseCmd(String macAddress, String closeCmd) {
        mSharedPreferences.edit().putString(PREF_CMD_CLOSE_PREFIX + macAddress, closeCmd).apply();
    }

    @Override
    public String getCloseCmd(String macAddress) {
        return mSharedPreferences.getString(PREF_CMD_CLOSE_PREFIX + macAddress, null);
    }

    @Override
    public void setDeviceNoAndMacAddress(String deviceNo, String macAddress) {
        mSharedPreferences.edit().putString(PREF_BLUETOOTH_MAC_ADDRESS_PREFIX + deviceNo, macAddress).apply();
    }

    @Override
    public String getMacAddressByDeviceNo(String deviceNo) {
        return mSharedPreferences.getString(PREF_BLUETOOTH_MAC_ADDRESS_PREFIX + deviceNo, null);
    }
}
