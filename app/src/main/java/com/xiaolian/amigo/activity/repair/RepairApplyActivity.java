package com.xiaolian.amigo.activity.repair;

import android.os.Bundle;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.activity.common.DeviceTypeActivity;
import com.xiaolian.amigo.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报修申请
 * <p>
 * Created by caidong on 2017/9/12.
 */
public class RepairApplyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_apply);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.rl_device)
    void selectDevice() {
        startActivity(this, DeviceTypeActivity.class);
    }
}