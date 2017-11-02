package com.xiaolian.amigo.ui.wallet;

import com.xiaolian.amigo.data.manager.intf.IWalletDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.QueryUserThirdAccountReqDTO;
import com.xiaolian.amigo.data.network.model.dto.request.SimpleReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.BooleanRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.QueryUserThirdAccountRespDTO;
import com.xiaolian.amigo.data.network.model.dto.response.UserThirdAccountDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.device.dispenser.ChooseDispenserPresenter;
import com.xiaolian.amigo.ui.wallet.adaptor.ChooseWithdrawAdapter;
import com.xiaolian.amigo.ui.wallet.intf.IChooseWithdrawPresenter;
import com.xiaolian.amigo.ui.wallet.intf.IChooseWithdrawView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 选择提现
 * <p>
 * Created by zcd on 10/27/17.
 */

public class ChooseWithdrawPresenter<V extends IChooseWithdrawView> extends BasePresenter<V>
        implements IChooseWithdrawPresenter<V> {
    private static final String TAG = ChooseDispenserPresenter.class.getSimpleName();
    private IWalletDataManager walletDataManager;

    @Inject
    public ChooseWithdrawPresenter(IWalletDataManager walletDataManager) {
        this.walletDataManager = walletDataManager;
    }

    @Override
    public void requestAccounts(int type) {
        QueryUserThirdAccountReqDTO reqDTO = new QueryUserThirdAccountReqDTO();
        reqDTO.setType(type);
        addObserver(walletDataManager.requestThirdAccounts(reqDTO), new NetworkObserver<ApiResult<QueryUserThirdAccountRespDTO>>() {

            @Override
            public void onReady(ApiResult<QueryUserThirdAccountRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getThirdAccounts() != null && result.getData().getThirdAccounts().size() > 0) {
                        List<ChooseWithdrawAdapter.Item> items = new ArrayList<>();
                        for (UserThirdAccountDTO dto : result.getData().getThirdAccounts()) {
                            items.add(new ChooseWithdrawAdapter.Item(dto));
                        }
                        getMvpView().addMore(items);
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }

            }
        });
    }

    @Override
    public void deleteAccount(Long id) {
        SimpleReqDTO reqDTO = new SimpleReqDTO();
        reqDTO.setId(id);
        addObserver(walletDataManager.deleteAccount(reqDTO), new NetworkObserver<ApiResult<BooleanRespDTO>>() {

            @Override
            public void onReady(ApiResult<BooleanRespDTO> result) {
                if (null == result.getError()) {
                    getMvpView().onSuccess("删除成功");
                    getMvpView().refreshList();
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }
}