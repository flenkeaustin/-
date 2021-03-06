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

package com.xiaolian.amigo.ui.repair.intf;


import com.xiaolian.amigo.di.RepairActivityContext;
import com.xiaolian.amigo.ui.base.intf.IBasePresenter;

/**
 * 报修列表
 *
 * @author caidong
 * @date 17/9/18
 */
@RepairActivityContext
public interface IRepairPresenter<V extends IRepairView> extends IBasePresenter<V> {

    /**
     * 刷新报修记录
     *
     * @param page 页数
     */
    void requestRepairs(int page);

    /**
     * 设置上次报修时间
     *
     * @param time 时间
     */
    void setLastRepairTime(Long time);
}
