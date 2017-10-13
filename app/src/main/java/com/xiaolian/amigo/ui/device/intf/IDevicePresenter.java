package com.xiaolian.amigo.ui.device.intf;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaolian.amigo.data.enumeration.TradeStep;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.response.CmdResultRespDTO;
import com.xiaolian.amigo.ui.base.intf.IBasePresenter;
import com.xiaolian.amigo.ui.base.intf.IBaseView;
import com.xiaolian.amigo.ui.ble.intf.IBleInteractiveView;
import com.xiaolian.amigo.ui.device.DeviceBasePresenter;

/**
 * Created by caidong on 2017/9/22.
 */
public interface IDevicePresenter<V extends IBaseView> extends IBasePresenter<V> {

    // 连接设备
    void onConnect(@NonNull String macAddress);

    // 重新连接设备
    void onReconnect(@NonNull String macAddress);

    // 向设备下发指令
    void onWrite(@NonNull String command);

    // 点击支付
    void onPay(int method, @Nullable Integer prepay, @Nullable Long bonusId);

    // 点击结束用水
    void onClose();

    // 接收设备通知（读数据）
    void registerNotify();

    // 断开连接
    void onDisConnect();

    // 处理蓝牙响应结果
    void handleResult(String data);

    // 处理网络请求响应结果
    void handleResult(ApiResult<CmdResultRespDTO> result);

    // 设置回调操作
    void setCallback(DeviceBasePresenter.Callback callback);

    // 设置当前页面操作步骤
    void setStep(TradeStep step);

    // 获取当前页面操作步骤
    TradeStep getStep();
}
