package com.xiaolian.amigo.ui.bonus;

import com.xiaolian.amigo.data.manager.intf.IBonusDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.bonus.Bonus;
import com.xiaolian.amigo.data.network.model.dto.request.QueryUserBonusReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.QueryUserBonusListRespDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.bonus.adaptor.BonusAdaptor;
import com.xiaolian.amigo.ui.bonus.intf.IBonusPresenter;
import com.xiaolian.amigo.ui.bonus.intf.IBonusView;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 红包Presenter实现类
 * @author zcd
 */
public class BonusPresenter<V extends IBonusView> extends BasePresenter<V>
        implements IBonusPresenter<V> {
    private static final String TAG = BonusPresenter.class.getSimpleName();
    private IBonusDataManager manager;

    @Inject
    public BonusPresenter(IBonusDataManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void requestBonusList(int page) {
        QueryUserBonusReqDTO dto = new QueryUserBonusReqDTO();
        dto.setPage(page);
        dto.setSize(Constant.PAGE_SIZE);
        // 显示红包 未使用1 未过期1 过期红包 未使用1 已过期2
        dto.setUseStatus(1);
        dto.setValidStatus(1);
        addObserver(manager.queryOrders(dto), new NetworkObserver<ApiResult<QueryUserBonusListRespDTO>>() {
            @Override
            public void onReady(ApiResult<QueryUserBonusListRespDTO> result) {
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                if (null == result.getError()) {
                    List<BonusAdaptor.BonusWrapper> wrappers = new ArrayList<>();
                    if (null != result.getData().getBonuses()) {
                        for (Bonus bonus : result.getData().getBonuses()) {
                            wrappers.add(new BonusAdaptor.BonusWrapper(bonus));
                        }
                        getMvpView().addMore(wrappers);
                        getMvpView().addPage();
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }

    @Override
    public void requestExpiredBonusList(int page) {
        QueryUserBonusReqDTO dto = new QueryUserBonusReqDTO();
        dto.setPage(page);
        dto.setSize(Constant.PAGE_SIZE);
        // 显示红包 未使用1 未过期1 过期红包 未使用1 已过期2
        dto.setUseStatus(1);
        dto.setValidStatus(2);
        addObserver(manager.queryOrders(dto), new NetworkObserver<ApiResult<QueryUserBonusListRespDTO>>() {
            @Override
            public void onReady(ApiResult<QueryUserBonusListRespDTO> result) {
                getMvpView().setRefreshComplete();
                getMvpView().setLoadMoreComplete();
                if (null == result.getError()) {
                    List<BonusAdaptor.BonusWrapper> wrappers = new ArrayList<>();
                    if (null != result.getData().getBonuses()) {
                        for (Bonus bonus : result.getData().getBonuses()) {
                            wrappers.add(new BonusAdaptor.BonusWrapper(bonus));
                        }
                        getMvpView().addMore(wrappers);
                        getMvpView().addPage();
                    }
                } else {
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });

    }
}
