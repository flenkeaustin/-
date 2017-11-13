package com.xiaolian.amigo.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.xiaolian.amigo.data.manager.OssDataManager;
import com.xiaolian.amigo.data.manager.UserDataManager;
import com.xiaolian.amigo.data.manager.intf.IOssDataManager;
import com.xiaolian.amigo.data.manager.intf.IUserDataManager;
import com.xiaolian.amigo.di.UserActivityContext;
import com.xiaolian.amigo.ui.user.CheckPasswordPresenter;
import com.xiaolian.amigo.ui.user.ChooseDormitoryPresenter;
import com.xiaolian.amigo.ui.user.EditAvatarPresenter;
import com.xiaolian.amigo.ui.user.EditDormitoryPresenter;
import com.xiaolian.amigo.ui.user.EditMobilePresenter;
import com.xiaolian.amigo.ui.user.EditNickNamePresenter;
import com.xiaolian.amigo.ui.user.EditPasswordPresenter;
import com.xiaolian.amigo.ui.user.EditProfilePresenter;
import com.xiaolian.amigo.ui.user.ListChoosePresenter;
import com.xiaolian.amigo.ui.user.intf.ICheckPasswordPresenter;
import com.xiaolian.amigo.ui.user.intf.ICheckPasswordView;
import com.xiaolian.amigo.ui.user.intf.IChooseDormitoryPresenter;
import com.xiaolian.amigo.ui.user.intf.IChooseDormitoryView;
import com.xiaolian.amigo.ui.user.intf.IEditAvatarPresenter;
import com.xiaolian.amigo.ui.user.intf.IEditAvatarVIew;
import com.xiaolian.amigo.ui.user.intf.IEditDormitoryPresenter;
import com.xiaolian.amigo.ui.user.intf.IEditDormitoryView;
import com.xiaolian.amigo.ui.user.intf.IEditMobilePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditMobileView;
import com.xiaolian.amigo.ui.user.intf.IEditNickNamePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditNickNameView;
import com.xiaolian.amigo.ui.user.intf.IEditPasswordPresenter;
import com.xiaolian.amigo.ui.user.intf.IEditPasswordView;
import com.xiaolian.amigo.ui.user.intf.IEditProfilePresenter;
import com.xiaolian.amigo.ui.user.intf.IEditProfileView;
import com.xiaolian.amigo.ui.user.intf.IListChoosePresenter;
import com.xiaolian.amigo.ui.user.intf.IListChooseView;

import dagger.Module;
import dagger.Provides;

/**
 * User模块ActivityModule
 * @author zcd
 */
@Module
public class UserActivityModule {

    private AppCompatActivity mActivity;

    public UserActivityModule(AppCompatActivity activity)
    {
        this.mActivity = activity;
    }

    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }


    @Provides
    IUserDataManager provideUserDataManager(UserDataManager manager) {
        return manager;
    }

    @Provides
    IOssDataManager provideOssDataManager(OssDataManager manager) {
        return manager;
    }

    @Provides
    @UserActivityContext
    IEditProfilePresenter<IEditProfileView> provideEditProfilePresenter(
            EditProfilePresenter<IEditProfileView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IEditNickNamePresenter<IEditNickNameView> provideEditNicknamePresenter(
            EditNickNamePresenter<IEditNickNameView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IEditAvatarPresenter<IEditAvatarVIew> provideEditAvatarPresenter(
            EditAvatarPresenter<IEditAvatarVIew> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IEditMobilePresenter<IEditMobileView> provideEditMobilePresenter(
            EditMobilePresenter<IEditMobileView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IEditPasswordPresenter<IEditPasswordView> provideEditPasswordPresenter(
            EditPasswordPresenter<IEditPasswordView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IListChoosePresenter<IListChooseView> provideListChoosePresenter(
            ListChoosePresenter<IListChooseView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IEditDormitoryPresenter<IEditDormitoryView> provideEditDormitoryPresenter(
            EditDormitoryPresenter<IEditDormitoryView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    ICheckPasswordPresenter<ICheckPasswordView> provideCheckPasswordPresenter(
            CheckPasswordPresenter<ICheckPasswordView> presenter) {
        return presenter;
    }

    @Provides
    @UserActivityContext
    IChooseDormitoryPresenter<IChooseDormitoryView> provideChooseDormitoryPresenter(
            ChooseDormitoryPresenter<IChooseDormitoryView> presenter) {
        return presenter;
    }
}
