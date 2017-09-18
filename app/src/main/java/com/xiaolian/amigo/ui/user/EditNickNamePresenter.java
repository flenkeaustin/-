package com.xiaolian.amigo.ui.user;

import com.xiaolian.amigo.data.manager.intf.IUserDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.dto.request.PersonalUpdateReqDTO;
import com.xiaolian.amigo.data.network.model.dto.response.EntireUserDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditNickNamePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditNickNameView;

import javax.inject.Inject;

/**
 * 编辑昵称Presenter
 * @author zcd
 */
public class EditNickNamePresenter<V extends IEditNickNameView> extends BasePresenter<V>
        implements IEditNickNamePresenter<V> {

    private IEditNickNameView mView;

    private IUserDataManager manager;

    @Inject
    public EditNickNamePresenter(IUserDataManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void updateNickName(String nickName) {
        PersonalUpdateReqDTO dto = new PersonalUpdateReqDTO();
        dto.setNickName(nickName);
        addObserver(manager.updateUserInfo(dto), new NetworkObserver<ApiResult<EntireUserDTO>>() {
            @Override
            public void onReady(ApiResult<EntireUserDTO> result) {
                if (null == result.getError()) {
                    getMvpView().showMessage("修改成功");
                } else {
                    getMvpView().showMessage(result.getError().getDebugMessage());
                }
            }
        });
    }

}
