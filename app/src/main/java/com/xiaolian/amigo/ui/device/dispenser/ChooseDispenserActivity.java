package com.xiaolian.amigo.ui.device.dispenser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.enumeration.TradeError;
import com.xiaolian.amigo.data.network.model.device.ScanDevice;
import com.xiaolian.amigo.data.network.model.device.ScanDeviceGroup;
import com.xiaolian.amigo.data.network.model.order.OrderPreInfoDTO;
import com.xiaolian.amigo.ui.device.DeviceBaseActivity;
import com.xiaolian.amigo.ui.device.WaterDeviceBaseActivity;
import com.xiaolian.amigo.ui.device.intf.dispenser.IChooseDispenerView;
import com.xiaolian.amigo.ui.device.intf.dispenser.IChooseDispenserPresenter;
import com.xiaolian.amigo.ui.main.MainActivity;
import com.xiaolian.amigo.ui.widget.SpaceItemDecoration;
import com.xiaolian.amigo.ui.widget.indicator.RefreshLayoutFooter;
import com.xiaolian.amigo.ui.widget.indicator.RefreshLayoutHeader;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.Log;
import com.xiaolian.amigo.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * 附近饮水机页面
 *
 * @author zcd
 */
public class ChooseDispenserActivity extends DeviceBaseActivity implements IChooseDispenerView {

    private static final String TAG = ChooseDispenserActivity.class.getSimpleName();
    public static final String INTENT_KEY_ACTION = "intent_key_action";
    public static final int ACTION_NORMAL = 0;
    public static final int ACTION_CHANGE_DISPENSER = 1;

    @Inject
    IChooseDispenserPresenter<IChooseDispenerView> presenter;
    ChooseDispenserAdaptor adaptor;
    /**
     * 列表显示的是附近列表还是收藏列表
     * false 表示附近列表
     * true 表示收藏列表
     */
    boolean listStatus = false;
    List<ChooseDispenserAdaptor.DispenserWrapper> items = new ArrayList<>();
    List<ChooseDispenserAdaptor.DispenserWrapper> nearbyItems = new ArrayList<>();
    List<ChooseDispenserAdaptor.DispenserWrapper> favoriteItems = new ArrayList<>();

    TextView tv_nearby;
    TextView tv_favorite;

    LinearLayout ll_footer;

    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;

    RelativeLayout rl_empty;
    RelativeLayout rl_error;
    int action = ACTION_NORMAL;
    private OrderPreInfoDTO orderPreInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_dispenser);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        presenter.onAttach(ChooseDispenserActivity.this);
        adaptor = new ChooseDispenserAdaptor(this, R.layout.item_dispenser,
                items, presenter, orderPreInfo);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dpToPxInt(this, 14)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
                if (listStatus) {
                    presenter.requestFavorites();
                } else {
                    presenter.onLoad();
                }
            }

            @Override
            public void onLoadmore(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {

            }
        });
        refreshLayout.setRefreshHeader(new RefreshLayoutHeader(this));
        refreshLayout.setRefreshFooter(new RefreshLayoutFooter(this));
        refreshLayout.setReboundDuration(200);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.autoRefresh(0);

        rl_empty = (RelativeLayout) findViewById(R.id.rl_empty);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);

        ll_footer = (LinearLayout) findViewById(R.id.ll_footer);

        tv_nearby = (TextView) findViewById(R.id.tv_toolbar_title);
        tv_nearby.setOnClickListener(v -> onNearbyClick());
        tv_favorite = (TextView) findViewById(R.id.tv_toolbar_title2);
        tv_favorite.setOnClickListener(v -> onFavoriteClick());
    }

    @Override
    protected void setUp() {
        super.setUp();
        if (getIntent() != null) {
            action = getIntent().getIntExtra(INTENT_KEY_ACTION, ACTION_NORMAL);
            orderPreInfo = getIntent().getParcelableExtra(WaterDeviceBaseActivity.INTENT_PREPAY_INFO);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void onNearbyClick() {
        if (listStatus) {
            switchListStatus();
            presenter.setListStatus(false);
            this.items.clear();
            hideEmptyView();
            hideErrorView();
            if (nearbyItems.isEmpty()) {
//                presenter.onLoad();
                refreshLayout.autoRefresh(0);
                adaptor.notifyDataSetChanged();
            } else {
                this.items.addAll(nearbyItems);
                adaptor.notifyDataSetChanged();
            }
        }
    }

    private void onFavoriteClick() {
        if (!listStatus) {
            switchListStatus();
            presenter.setListStatus(true);
            this.items.clear();
            if (favoriteItems.isEmpty()) {
                presenter.requestFavorites();
            } else {
                hideEmptyView();
                hideErrorView();
                this.items.addAll(favoriteItems);
                adaptor.notifyDataSetChanged();
//                adaptor.notifyItemRangeChanged(0, items.size());
//                recyclerView.setAdapter(adaptor);
            }
        }
    }


    private void switchListStatus() {
        if (listStatus) {
            ll_footer.setVisibility(View.VISIBLE);
            listStatus = false;
            tv_nearby.setTextColor(ContextCompat.getColor(this, R.color.colorDark2));
            tv_favorite.setTextColor(ContextCompat.getColor(this, R.color.colorDarkB));
        } else {
            ll_footer.setVisibility(View.GONE);
            listStatus = true;
            tv_nearby.setTextColor(ContextCompat.getColor(this, R.color.colorDarkB));
            tv_favorite.setTextColor(ContextCompat.getColor(this, R.color.colorDark2));
        }
    }

    @Override
    public void addMore(List<ChooseDispenserAdaptor.DispenserWrapper> wrappers) {
        if (wrappers.isEmpty() && !listStatus) {
            return;
        }
        items.addAll(wrappers);
        favoriteItems.addAll(wrappers);
//        if (recyclerView.getAdapter() == null) {
//        }
        adaptor.notifyDataSetChanged();
    }

    @Override
    public void addScanDevices(List<ScanDeviceGroup> devices) {
        updateDevice(devices);
        Log.i(TAG, "扫描到的设备列表：" + devices);
    }

    @Override
    public void showEmptyView() {
//        recyclerView.setVisibility(View.GONE);
        items.clear();
        adaptor.notifyDataSetChanged();
        rl_empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
//        recyclerView.setVisibility(View.VISIBLE);
        rl_empty.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
//        recyclerView.setVisibility(View.GONE);
        items.clear();
        adaptor.notifyDataSetChanged();
        rl_error.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorView() {
//        recyclerView.setVisibility(View.VISIBLE);
        rl_error.setVisibility(View.GONE);
    }

    @Override
    public void completeRefresh() {
        refreshLayout.finishRefresh(100);
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void gotoDispenser(String macAddress, boolean favor, Long residenceId, String usefor, String location) {
        startActivity(new Intent(this, DispenserActivity.class)
                .putExtra(MainActivity.INTENT_KEY_MAC_ADDRESS,
                        macAddress)
                .putExtra(DispenserActivity.INTENT_KEY_FAVOR,
                        favor)
                .putExtra(DispenserActivity.INTENT_KEY_ID,
                        residenceId)
                .putExtra(DispenserActivity.INTENT_KEY_TEMPERATURE,
                        usefor)
                .putExtra(MainActivity.INTENT_KEY_LOCATION, location)
                .putExtra(MainActivity.INTENT_KEY_DEVICE_TYPE, Device.DISPENSER.getType())
                .putExtra(WaterDeviceBaseActivity.INTENT_PREPAY_INFO, orderPreInfo));
        finish();
    }

    private synchronized void updateDevice(List<ScanDeviceGroup> devices) {
        if (devices.isEmpty()) {
//            if (nearbyItems.isEmpty()) {
//                showEmptyView();
//            }
            return;
        }
        if (nearbyItems.isEmpty()) {
            for (ScanDeviceGroup scanDeviceGroup : devices) {
                nearbyItems.add(new ChooseDispenserAdaptor.DispenserWrapper(scanDeviceGroup));
            }
            if (!listStatus) {
                items.addAll(nearbyItems);
                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(adaptor);
                }
                adaptor.notifyDataSetChanged();
            }
        } else {
            boolean needNotify = false;
            List<ChooseDispenserAdaptor.DispenserWrapper> tempItems = null;
            for (ScanDeviceGroup scanDeviceGroup : devices) {
                boolean isContained = false;
                for (ChooseDispenserAdaptor.DispenserWrapper wrapper : nearbyItems) {
                    if (CommonUtil.equals(scanDeviceGroup.getResidenceId(),
                            wrapper.getResidenceId())) {
                        isContained = true;
                        for (ScanDevice device : scanDeviceGroup.getWater()) {
                            if (!isContainDevice(device, wrapper.getDeviceGroup())) {
                                needNotify = true;
                                if (wrapper.getDeviceGroup().getWater() != null) {
                                    wrapper.getDeviceGroup().getWater().add(device);
                                } else {
                                    List<ScanDevice> water = new ArrayList<>();
                                    water.add(device);
                                    wrapper.getDeviceGroup().setWater(water);
                                }
                            }
                        }
                    }
                }
                if (!isContained) {
                    if (tempItems == null) {
                        tempItems = new ArrayList<>();
                    }
                    tempItems.add(new ChooseDispenserAdaptor.DispenserWrapper(scanDeviceGroup));
                    needNotify = true;
                }
            }
            if (tempItems != null) {
                nearbyItems.addAll(tempItems);
            }
            if (needNotify && !listStatus) {
                items.clear();
                items.addAll(nearbyItems);
                adaptor.notifyDataSetChanged();
            }
        }
    }

    private boolean isContainDevice(ScanDevice device, ScanDeviceGroup deviceGroup) {
        for (ScanDevice contained : deviceGroup.getWater()) {
            if (CommonUtil.equals(contained.getId(), device.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        presenter.closeBleConnection();
        super.onPause();
    }

    @Override
    public void onError(TradeError tradeError) {

    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
