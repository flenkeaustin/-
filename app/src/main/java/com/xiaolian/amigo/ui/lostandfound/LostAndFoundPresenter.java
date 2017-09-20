package com.xiaolian.amigo.ui.lostandfound;

import com.xiaolian.amigo.data.manager.intf.ILostAndFoundDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.QueryLostAndFoundListReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.QueryLostAndFoundListRespDTO;
import com.xiaolian.amigo.data.network.model.lostandfound.LostAndFound;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.lostandfound.adapter.LostAndFoundAdaptor;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundPresenter;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 失物招领
 * <p>
 * Created by zcd on 9/18/17.
 */

public class LostAndFoundPresenter<V extends ILostAndFoundView> extends BasePresenter<V>
        implements ILostAndFoundPresenter<V> {

    ILostAndFoundDataManager manager;

    @Inject
    public LostAndFoundPresenter(ILostAndFoundDataManager manager) {
        this.manager = manager;
    }


    @Override
    public void queryLostAndFoundList(int page, Long schoolId, String selectKey, int size, int type) {
        QueryLostAndFoundListReqDTO dto = new QueryLostAndFoundListReqDTO();
        dto.setPage(page);
        dto.setSchoolId(schoolId);
        dto.setSelectKey(selectKey);
        dto.setSize(size);
        dto.setType(type);
        addObserver(manager.queryLostAndFounds(dto), new NetworkObserver<ApiResult<QueryLostAndFoundListRespDTO>>() {

            @Override
            public void onReady(ApiResult<QueryLostAndFoundListRespDTO> result) {
                if (null == result.getError()) {

                    List<LostAndFoundAdaptor.LostAndFoundWapper> wrappers = new ArrayList<>();
                    if (null != result.getData().getLostAndFounds() && result.getData().getLostAndFounds().size() > 0) {
                        for (LostAndFound lost : result.getData().getLostAndFounds()) {
                            wrappers.add(new LostAndFoundAdaptor.LostAndFoundWapper(lost));
                        }
                        getMvpView().addMoreLost(wrappers);
                    }
                } else {
                    getMvpView().showMessage(result.getError().getDisplayMessage());
                }
            }
        });
    }

}
