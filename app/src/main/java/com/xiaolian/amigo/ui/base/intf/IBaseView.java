/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.xiaolian.amigo.ui.base.intf;


import android.support.annotation.StringRes;

public interface IBaseView {

    void showLoading();

    void hideLoading();

    void openActivityOnTokenExpire();

    void onError(@StringRes int resId);

    void onError(String message);

    void onSuccess(@StringRes int resId);

    void onSuccess(String message);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    boolean isWifiConnected();

    void hideKeyboard();

    // 打开蓝牙，获取蓝牙设备权限
    void getBLEPermission();

    void redirectToLogin();
}
