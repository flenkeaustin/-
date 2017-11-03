package com.xiaolian.amigo.ui.wallet;

import android.text.TextUtils;

import com.xiaolian.amigo.data.manager.intf.IWalletDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.QueryUserThirdAccountReqDTO;
import com.xiaolian.amigo.data.network.model.dto.request.WithdrawReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.QueryUserThirdAccountRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.SimpleRespDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.wallet.intf.IWithdrawalPresenter;
import com.xiaolian.amigo.ui.wallet.intf.IWithdrawalView;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * 提现
 * <p>
 * Created by zcd on 10/14/17.
 */

public class WithdrawalPresenter<V extends IWithdrawalView> extends BasePresenter<V>
        implements IWithdrawalPresenter<V> {
    IWalletDataManager walletDataManager;

    @Inject
    public WithdrawalPresenter(IWalletDataManager manager) {
        this.walletDataManager = manager;
    }

    @Override
    public void withdraw(String amount, Long withdrawId) {
        WithdrawReqDTO reqDTO = new WithdrawReqDTO();
        reqDTO.setAmount(amount);
        reqDTO.setUserThirdAccountId(withdrawId);
        addObserver(walletDataManager.withdraw(reqDTO), new NetworkObserver<ApiResult<SimpleRespDTO>>() {

            @Override
            public void onReady(ApiResult<SimpleRespDTO> result) {
                if (null == result.getError()) {
//                    getMvpView().onSuccess("提现成功");
                    getMvpView().gotoWithdrawDetail(result.getData().getId());
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public void requestAccounts(int type) {
        String withdrawName = walletDataManager.getLastWithdrawName();
        Long withdrawId = walletDataManager.getLastWithdrawId();
        if (withdrawId != -1 && !TextUtils.isEmpty(withdrawName)) {
            getMvpView().showWithdrawAccount(withdrawName, withdrawId);
            return;
        }
        QueryUserThirdAccountReqDTO reqDTO = new QueryUserThirdAccountReqDTO();
        reqDTO.setType(type);
        addObserver(walletDataManager.requestThirdAccounts(reqDTO), new NetworkObserver<ApiResult<QueryUserThirdAccountRespDTO>>() {

            @Override
            public void onReady(ApiResult<QueryUserThirdAccountRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getThirdAccounts() != null && result.getData().getThirdAccounts().size() > 0) {
                        String withdrawName = result.getData().getThirdAccounts().get(0).getAccountName();
                        Long withdrawId = result.getData().getThirdAccounts().get(0).getId();
                        getMvpView().showWithdrawAccount(withdrawName, withdrawId);
                        walletDataManager.setLastWithdrawId(withdrawId);
                        walletDataManager.setLastWithdrawName(withdrawName);
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }

            }
        });
    }

}
