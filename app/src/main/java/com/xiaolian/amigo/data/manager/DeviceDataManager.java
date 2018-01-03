package com.xiaolian.amigo.data.manager;

import com.xiaolian.amigo.data.manager.intf.IDeviceDataManager;
import com.xiaolian.amigo.data.network.ICsApi;
import com.xiaolian.amigo.data.network.IDeviceApi;
import com.xiaolian.amigo.data.network.IFundsApi;
import com.xiaolian.amigo.data.network.IOrderApi;
import com.xiaolian.amigo.data.network.ITradeApi;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.common.SimpleQueryReqDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleRespDTO;
import com.xiaolian.amigo.data.network.model.cs.CsMobileRespDTO;
import com.xiaolian.amigo.data.network.model.device.QueryDeviceListReqDTO;
import com.xiaolian.amigo.data.network.model.device.QueryDeviceListRespDTO;
import com.xiaolian.amigo.data.network.model.device.QueryFavorDeviceRespDTO;
import com.xiaolian.amigo.data.network.model.device.QueryWaterListRespDTO;
import com.xiaolian.amigo.data.network.model.funds.PersonalWalletDTO;
import com.xiaolian.amigo.data.network.model.order.OrderPreInfoDTO;
import com.xiaolian.amigo.data.network.model.order.QueryPrepayOptionReqDTO;
import com.xiaolian.amigo.data.network.model.order.UnsettledOrderStatusCheckReqDTO;
import com.xiaolian.amigo.data.network.model.order.UnsettledOrderStatusCheckRespDTO;
import com.xiaolian.amigo.data.network.model.trade.CmdResultReqDTO;
import com.xiaolian.amigo.data.network.model.trade.CmdResultRespDTO;
import com.xiaolian.amigo.data.network.model.trade.ConnectCommandReqDTO;
import com.xiaolian.amigo.data.network.model.trade.ConnectCommandRespDTO;
import com.xiaolian.amigo.data.network.model.trade.PayReqDTO;
import com.xiaolian.amigo.data.network.model.trade.PayRespDTO;
import com.xiaolian.amigo.data.prefs.ISharedPreferencesHelp;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * 设备数据管理
 * <p>
 * Created by zcd on 9/29/17.
 */

public class DeviceDataManager implements IDeviceDataManager {
    private static final String TAG = DeviceDataManager.class.getSimpleName();

    private ISharedPreferencesHelp sharedPreferencesHelp;
    private ITradeApi tradeApi;
    private IOrderApi orderApi;
    private IFundsApi fundsApi;
    private ICsApi csApi;
    private IDeviceApi deviceApi;

    @Inject
    public DeviceDataManager(Retrofit retrofit, ISharedPreferencesHelp sharedPreferencesHelp) {
        this.sharedPreferencesHelp  = sharedPreferencesHelp;
        tradeApi = retrofit.create(ITradeApi.class);
        orderApi = retrofit.create(IOrderApi.class);
        fundsApi = retrofit.create(IFundsApi.class);
        csApi = retrofit.create(ICsApi.class);
        deviceApi = retrofit.create(IDeviceApi.class);
    }
    @Override
    public void setBonusAmount(int amount) {
        sharedPreferencesHelp.setBonusAmount(amount);
    }

    @Override
    public int getBonusAmount() {
        return sharedPreferencesHelp.getBonusAmount();
    }

    @Override
    public String getMacAddressByDeviceNo(String macAddress) {
        return sharedPreferencesHelp.getMacAddressByDeviceNo(macAddress);
    }

    @Override
    public void setDeviceNoAndMacAddress(String macAddress, String currentMacAddress) {
        sharedPreferencesHelp.setDeviceNoAndMacAddress(macAddress, currentMacAddress);
    }

    @Override
    public void setDeviceToken(String deviceNo, String deviceToken) {
        sharedPreferencesHelp.setDeviceToken(deviceNo, deviceToken);
    }

    @Override
    public Observable<ApiResult<ConnectCommandRespDTO>> getConnectCommand(ConnectCommandReqDTO reqDTO) {
        sharedPreferencesHelp.setCurrentDeviceToken(sharedPreferencesHelp.getDeviceToken(reqDTO.getMacAddress()));
        return tradeApi.getConnectCommand(reqDTO);
    }

    @Override
    public Observable<ApiResult<CmdResultRespDTO>> processCmdResult(CmdResultReqDTO reqDTO) {
        sharedPreferencesHelp.setCurrentDeviceToken(sharedPreferencesHelp.getDeviceToken(reqDTO.getMacAddress()));
        return tradeApi.processCmdResult(reqDTO);
    }

    @Override
    public Observable<ApiResult<PayRespDTO>> pay(PayReqDTO reqDTO) {
        sharedPreferencesHelp.setCurrentDeviceToken(sharedPreferencesHelp.getDeviceToken(reqDTO.getMacAddress()));
        return tradeApi.pay(reqDTO);
    }

    @Override
    public Observable<ApiResult<QueryDeviceListRespDTO>> handleScanDevices(QueryDeviceListReqDTO reqDTO) {
        return deviceApi.getDeviceList(reqDTO);
    }

    @Override
    public Observable<ApiResult<UnsettledOrderStatusCheckRespDTO>> checkOrderStatus(UnsettledOrderStatusCheckReqDTO reqDTO) {
        return orderApi.checkOrderStatus(reqDTO);
    }

    @Override
    public void setDeviceResult(String deviceNo, String deviceResult) {
        sharedPreferencesHelp.setDeviceResult(deviceNo, deviceResult);
    }

    @Override
    public String getDeviceResult(String deviceNo) {
        return sharedPreferencesHelp.getDeviceResult(deviceNo);
    }

    @Override
    public void setCloseCmd(String deviceNo, String closeCmd) {
        sharedPreferencesHelp.setCloseCmd(deviceNo, closeCmd);
    }

    @Override
    public String getCloseCmd(String deviceNo) {
        return sharedPreferencesHelp.getCloseCmd(deviceNo);
    }

    @Override
    public Observable<ApiResult<PersonalWalletDTO>> queryWallet() {
        return fundsApi.queryWallet();
    }

    @Override
    public Observable<ApiResult<OrderPreInfoDTO>> queryPrepayOption(QueryPrepayOptionReqDTO reqDTO) {
        return orderApi.queryPrepayOption(reqDTO);
    }

    @Override
    public Observable<ApiResult<CsMobileRespDTO>> queryCsInfo() {
        return csApi.queryCsInfo();
    }

    @Override
    public boolean isHeaterGuideDone() {
        return !(sharedPreferencesHelp.getHeaterGuide() != null
                && sharedPreferencesHelp.getHeaterGuide() < 3);
    }

    @Override
    public void doneHeaterGuide() {
        sharedPreferencesHelp.setHeaterGuide(sharedPreferencesHelp.getHeaterGuide() + 1);
    }

    @Override
    public void setHeaterGuide(Integer guideTime) {
        sharedPreferencesHelp.setHeaterGuide(guideTime);
    }

    @Override
    public boolean isDispenserGuideDone() {
        return !(sharedPreferencesHelp.getDispenserGuide() != null
                && sharedPreferencesHelp.getDispenserGuide() < 3);
    }

    @Override
    public void doneDispenserGuide() {
        sharedPreferencesHelp.setDispenserGuide(sharedPreferencesHelp.getDispenserGuide() + 1);
    }

    @Override
    public void setDispenserGuide(Integer guideTime) {
        sharedPreferencesHelp.setDispenserGuide(guideTime);
    }

    @Override
    public Observable<ApiResult<SimpleRespDTO>> favorite(SimpleReqDTO reqDTO) {
        return deviceApi.favorite(reqDTO);
    }

    @Override
    public Observable<ApiResult<SimpleRespDTO>> unFavorite(SimpleReqDTO reqDTO) {
        return deviceApi.unFavorite(reqDTO);
    }

    @Override
    public Observable<ApiResult<QueryFavorDeviceRespDTO>> getFavorites(QueryDeviceListReqDTO reqDTO) {
        return deviceApi.getFavorites(reqDTO);
    }
}
