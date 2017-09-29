package com.xiaolian.amigo.ui.device.intf.heator;

import com.xiaolian.amigo.data.manager.intf.IBleDataManager;
import com.xiaolian.amigo.data.manager.intf.IDeviceDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.response.PersonalWalletDTO;
import com.xiaolian.amigo.ui.device.DeviceBasePresenter;

import javax.inject.Inject;

/**
 * Created by caidong on 2017/9/22.
 */
public class HeaterPresenter<V extends IHeaterView> extends DeviceBasePresenter<V>
        implements IHeaterPresenter<V> {
    private IDeviceDataManager deviceDataManager;

    @Inject
    HeaterPresenter(IBleDataManager manager, IDeviceDataManager deviceDataManager) {
        super(manager);
        this.deviceDataManager = deviceDataManager;
    }

    @Override
    public void setBonusAmount(int amount) {
        deviceDataManager.setBonusAmount(amount);
    }

    @Override
    public int getBonusAmount() {
        return deviceDataManager.getBonusAmount();
    }

    @Override
    public void queryWallet(int amount) {
        addObserver(deviceDataManager.queryWallet(), new NetworkObserver<ApiResult<PersonalWalletDTO>>() {

            @Override
            public void onReady(ApiResult<PersonalWalletDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getBalance() >= amount) {
                        getMvpView().startUse();
                    } else {
                        getMvpView().showRechargeDialog(amount);
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }
}