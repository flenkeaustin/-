package com.xiaolian.amigo.ui.wallet;

import android.text.TextUtils;

import com.xiaolian.amigo.data.enumeration.AlipayPayOrderCheckResult;
import com.xiaolian.amigo.data.manager.intf.IWalletDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.AlipayTradeAppPayArgsReqDTO;
import com.xiaolian.amigo.data.network.model.dto.request.AlipayTradeAppPayResultParseReqDTO;
import com.xiaolian.amigo.data.network.model.dto.request.RechargeReqDTO;
import com.xiaolian.amigo.data.network.model.dto.request.SimpleQueryReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.AlipayTradeAppPayArgsRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.AlipayTradeAppPayResultParseRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.QueryRechargeAmountsRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.SimpleRespDTO;
import com.xiaolian.amigo.data.network.model.wallet.RechargeDenominations;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.wallet.adaptor.RechargeAdaptor;
import com.xiaolian.amigo.ui.wallet.intf.IRechargePresenter;
import com.xiaolian.amigo.ui.wallet.intf.IRechargeView;
import com.xiaolian.amigo.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * 充值
 * <p>
 * Created by zcd on 9/20/17.
 */

public class RechargePresenter<V extends IRechargeView> extends BasePresenter<V>
        implements IRechargePresenter<V> {
    private static final String TAG = RechargePresenter.class.getSimpleName();
    private IWalletDataManager manager;
    private Long fundsId;

    @Inject
    public RechargePresenter(IWalletDataManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void getRechargeList() {
        addObserver(manager.queryRechargeAmountList(new SimpleQueryReqDTO()), new NetworkObserver<ApiResult<QueryRechargeAmountsRespDTO>>() {

            @Override
            public void onReady(ApiResult<QueryRechargeAmountsRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getRechargeDenominations() != null
                            && result.getData().getRechargeDenominations().size() > 0) {
                        // 上次选择的amount
                        String lastAmount = manager.getLastRechargeAmount();
                        List<RechargeAdaptor.RechargeWrapper> rechargeWrapper = new ArrayList<>();
                        for (RechargeDenominations rechargeDenominations : result.getData().getRechargeDenominations()) {
                            if (TextUtils.equals(lastAmount, String.format(Locale.getDefault(), "%.2f",
                                    rechargeDenominations.getAmount()))) {
                                rechargeWrapper.add(
                                        new RechargeAdaptor.RechargeWrapper(rechargeDenominations, true));
                                getMvpView().enableRecharge();
                            } else {
                                rechargeWrapper.add(
                                        new RechargeAdaptor.RechargeWrapper(rechargeDenominations));
                            }
                        }
                        getMvpView().addMore(rechargeWrapper);
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public void recharge(Double amount, int type) {
        String _amount = String.format(Locale.getDefault(),"%.2f", amount);
        manager.setLastRechargeAmount(_amount);
        Log.i(TAG, "储存充值amount: " + _amount);
        RechargeReqDTO reqDTO = new RechargeReqDTO();
//        reqDTO.setDenominationId(id);
        reqDTO.setAmount(amount);
        reqDTO.setThirdAccountType(type);
        addObserver(manager.recharge(reqDTO), new NetworkObserver<ApiResult<SimpleRespDTO>>() {

            @Override
            public void onReady(ApiResult<SimpleRespDTO> result) {
                if (null == result.getError()) {
                    requestAlipayArgs(result.getData().getId());
//                    getMvpView().onSuccess("充值成功");
//                    getMvpView().back();
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    private void requestAlipayArgs(Long fundsId) {
        this.fundsId = fundsId;
        AlipayTradeAppPayArgsReqDTO reqDTO = new AlipayTradeAppPayArgsReqDTO();
        reqDTO.setFundsId(fundsId);
        addObserver(manager.requestAlipayArgs(reqDTO), new NetworkObserver<ApiResult<AlipayTradeAppPayArgsRespDTO>>(){

            @Override
            public void onReady(ApiResult<AlipayTradeAppPayArgsRespDTO> result) {
                if (null == result.getError()) {
                    Log.d(TAG, result.getData().getReqArgs());
                    getMvpView().alipay(result.getData().getReqArgs());
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }

            }
        });
    }

    @Override
    public void parseAlipayResult(String resultStatus, String result, String memo) {
        AlipayTradeAppPayResultParseReqDTO reqDTO = new AlipayTradeAppPayResultParseReqDTO();
        reqDTO.setMemo(memo);
        reqDTO.setResult(result);
        reqDTO.setResultStatus(resultStatus);
        reqDTO.setFundId(fundsId);
        addObserver(manager.parseAlipayResule(reqDTO), new NetworkObserver<ApiResult<AlipayTradeAppPayResultParseRespDTO>>() {

            @Override
            public void onReady(ApiResult<AlipayTradeAppPayResultParseRespDTO> apiResult) {
                if (apiResult.getError() == null) {
                    if (apiResult.getData().getCode() == AlipayPayOrderCheckResult.SUCCESS.getType()) {
                        getMvpView().onSuccess("充值成功");
                        getMvpView().gotoDetail(fundsId);
                    } else if (apiResult.getData().getCode() == AlipayPayOrderCheckResult.CANCEL.getType()) {
                        // ignore cancel
                    } else {
                        getMvpView().onError(apiResult.getData().getMsg());
                    }
                } else {
                    getMvpView().onError(apiResult.getError().getDisplayMessage());
                }
            }
        });
    }
}
