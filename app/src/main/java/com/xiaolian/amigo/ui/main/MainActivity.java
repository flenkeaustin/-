package com.xiaolian.amigo.ui.main;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.network.model.dto.response.DeviceCheckRespDTO;
import com.xiaolian.amigo.data.network.model.order.Order;
import com.xiaolian.amigo.data.network.model.user.BriefSchoolBusiness;
import com.xiaolian.amigo.ui.device.dispenser.ChooseDispenserActivity;
import com.xiaolian.amigo.ui.device.dispenser.DispenserActivity;
import com.xiaolian.amigo.ui.device.heater.HeaterActivity;
import com.xiaolian.amigo.ui.login.LoginActivity;
import com.xiaolian.amigo.ui.lostandfound.LostAndFoundActivity;
import com.xiaolian.amigo.ui.main.adaptor.HomeAdaptor;
import com.xiaolian.amigo.ui.main.intf.IMainPresenter;
import com.xiaolian.amigo.ui.main.intf.IMainView;
import com.xiaolian.amigo.ui.notice.NoticeListActivity;
import com.xiaolian.amigo.ui.user.EditProfileActivity;
import com.xiaolian.amigo.ui.wallet.PrepayActivity;
import com.xiaolian.amigo.ui.widget.dialog.AvailabilityDialog;
import com.xiaolian.amigo.ui.widget.dialog.NoticeAlertDialog;
import com.xiaolian.amigo.ui.widget.dialog.PrepayDialog;
import com.xiaolian.amigo.util.CommonUtil;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Data;

import static com.xiaolian.amigo.data.enumeration.Device.DISPENSER;
import static com.xiaolian.amigo.data.enumeration.Device.HEARTER;

/**
 * Created by yik on 2017/9/5.
 */

public class MainActivity extends MainBaseActivity implements IMainView {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_KEY_MAC_ADDRESS = "intent_key_mac_address";
    public static final String INTENT_KEY_LOCATION = "intent_key_location";
    public static final String INTENT_KEY_DEVICE_TYPE = "intent_key_device_type";
    public static final String INTENT_KEY_RESIDENCE_ID = "intent_key_residence_id";

    @Inject
    IMainPresenter<IMainView> presenter;

    @BindView(R.id.bt_switch)
    ImageView btSwitch;

    @BindView(R.id.tv_nickName)
    TextView tv_nickName;

    @BindView(R.id.tv_schoolName)
    TextView tv_schoolName;

    /**
     * 头像
     */
    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;

    /**
     * 通知数量上标
     */
    @BindView(R.id.tv_notice_count)
    TextView tv_notice_count;

    /**
     * 通知
     */
    @BindView(R.id.rl_notice)
    RelativeLayout rl_notice;

    @BindView(R.id.sv_main)
    ScrollView sl_main;

    private GestureDetector mGestureDetector;

    HomeFragment2 homeFragment;
    ProfileFragment2 profileFragment;

    int current = 0;
    private boolean hasBanners;
    List<BriefSchoolBusiness> businesses;
    private AvailabilityDialog availabilityDialog;
    private int heaterOrderSize;
    private int dispenserOrderSize;
    private PrepayDialog prepayDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);

        presenter.onAttach(this);

        btSwitch.setBackgroundResource(R.drawable.profile);

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment2();
            getSupportFragmentManager().beginTransaction().add(R.id.fm_container, homeFragment).commit();
        } else {
            HomeFragment2 home = (HomeFragment2) getSupportFragmentManager().findFragmentByTag("home");
            ProfileFragment2 profile = (ProfileFragment2) getSupportFragmentManager().findFragmentByTag("profile");
            if (home != null && profile != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(home)
                        .hide(profile)
                        .commit();
            }
        }

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//                if(Math.abs(e1.getRawY() - e2.getRawY())>100){
//                    //Toast.makeText(getApplicationContext(), "动作不合法", 0).show();
//                    return true;
//                }
//                if(Math.abs(velocityX)<150){
//                    //Toast.makeText(getApplicationContext(), "移动的太慢", 0).show();
//                    return true;
//                }
                if (hasBanners && current == 0) {
                    Banner banner = (Banner) sl_main.getRootView().findViewById(R.id.banner);
                    RectF rectF = CommonUtil.calcViewScreenLocation(banner);
                    if (rectF.contains(e1.getRawX(), e1.getRawY())) {
                        return false;
                    }
                }
                if ((e1.getRawX() - e2.getRawX()) > 200) {
                    if (current == 1) {
                        onSwitch();
                    }
                    return true;
                }

                if ((e2.getRawX() - e1.getRawX()) > 200) {
                    if (current == 0) {
                        onSwitch();
                    }
                    return true;//消费掉当前事件  不让当前事件继续向下传递
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        presenter.getSchoolBusiness();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 请求通知
        presenter.getNoticeAmount();
        if (!presenter.isLogin()) {
            tv_nickName.setText("登录／注册");
            tv_schoolName.setText("登录以后才能使用哦");
            iv_avatar.setImageResource(R.drawable.ic_picture_error);
        } else {
            // 设置昵称
            tv_nickName.setText(presenter.getUserInfo().getNickName());
            // 设置学校
            tv_schoolName.setText(presenter.getUserInfo().getSchoolName());
            // 设置头像
            if (presenter.getUserInfo().getPictureUrl() != null) {
                Glide.with(this).load(presenter.getUserInfo().getPictureUrl())
                        .asBitmap()
                        .placeholder(R.drawable.ic_picture_error)
                        .error(R.drawable.ic_picture_error)
                        .into(iv_avatar);
            } else {
                iv_avatar.setImageResource(R.drawable.ic_picture_error);
            }
            // 请求待找零订单
            presenter.getPrepayOrder();
        }
    }

    @OnClick(R.id.bt_switch)
    void onSwitch() {
        ImageView imageView = btSwitch;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (current == 0) {
            if (profileFragment == null) {
                profileFragment = new ProfileFragment2();
            }
            if (!profileFragment.isAdded()) {
                transaction.hide(homeFragment).add(R.id.fm_container, profileFragment, "profile").commit();
            } else {
                transaction.hide(homeFragment).show(profileFragment).commit();
            }
//            imageView.setBackgroundResource(R.drawable.home);
            imageViewAnimatedChange(this, imageView, R.drawable.home);
            current = 1;
            // 改为切换不隐藏
//            rl_notice.setVisibility(View.GONE);
        } else {
            if (homeFragment == null) {
                homeFragment = new HomeFragment2();
            }
            if (!homeFragment.isAdded()) {
                transaction.hide(profileFragment).add(R.id.fm_container, homeFragment, "home").commit();
            } else {
                transaction.hide(profileFragment).show(homeFragment).commit();
            }
//            imageView.setBackgroundResource(R.drawable.profile);
            imageViewAnimatedChange(this, imageView, R.drawable.profile);
            current = 0;
            // 改为切换不隐藏
//            rl_notice.setVisibility(View.VISIBLE);
        }


    }

    public void imageViewAnimatedChange(Context context, ImageView imageView, int res) {
        final Animation anim_out_right = AnimationUtils.loadAnimation(context, R.anim.item_slide_out_right);
        final Animation anim_out_left = AnimationUtils.loadAnimation(context, R.anim.item_slide_out_left);
        final Animation anim_in_left  = AnimationUtils.loadAnimation(context, R.anim.item_slide_in_left);
        final Animation anim_in_right  = AnimationUtils.loadAnimation(context, R.anim.item_slide_in_right);
        if (current == 0) {
            anim_out_right.setAnimationListener(new Animation.AnimationListener()
            {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation)
                {
                    imageView.setBackgroundResource(res);
                    anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                        @Override public void onAnimationStart(Animation animation) {}
                        @Override public void onAnimationRepeat(Animation animation) {}
                        @Override public void onAnimationEnd(Animation animation) {}
                    });
                    imageView.startAnimation(anim_in_right);
                }
            });
            imageView.startAnimation(anim_out_right);
        } else {
            anim_out_left.setAnimationListener(new Animation.AnimationListener()
            {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation)
                {
                    imageView.setBackgroundResource(res);
                    anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                        @Override public void onAnimationStart(Animation animation) {}
                        @Override public void onAnimationRepeat(Animation animation) {}
                        @Override public void onAnimationEnd(Animation animation) {}
                    });
                    imageView.startAnimation(anim_in_left);
                }
            });
            imageView.startAnimation(anim_out_left);

        }
    }


    @OnClick({R.id.iv_avatar, R.id.ll_user_info})
    void gotoLoginView() {
        if (!presenter.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 通知
     */
    @OnClick(R.id.iv_notice)
    void gotoNoticeList() {
        startActivity(new Intent(this, NoticeListActivity.class));
    }

    /**
     * 点击头像 跳转到编辑个人信息页面
     */
    @OnClick(R.id.iv_avatar)
    void onAvatarClick() {
        startActivity(this, EditProfileActivity.class);
    }

    /**
     * 点击昵称 跳转到编辑个人信息页面
     */
    @OnClick(R.id.ll_user_info)
    void onUserInfoClick() {
        startActivity(this, EditProfileActivity.class);
    }


    /**
     * 显示通知个数
     *
     * @param amount 通知个数
     */
    @Override
    public void showNoticeAmount(Integer amount) {
        if (amount != null && amount != 0) {
            tv_notice_count.setVisibility(View.VISIBLE);
            tv_notice_count.setText(String.valueOf(amount));
        } else {
            tv_notice_count.setVisibility(View.GONE);
        }
    }

    @Override
    public void showTimeValidDialog(String title, String remark, int deviceType) {
        if (null == availabilityDialog) {
            availabilityDialog = new AvailabilityDialog(this);
        }
        if (availabilityDialog.isShowing()) {
            return;
        }
        availabilityDialog.setOkText(getString(R.string.keep_use));
        availabilityDialog.setTip(title);
        availabilityDialog.setSubTip(remark);
        availabilityDialog.setOnOkClickListener(dialog1 -> {
            if (deviceType == 1) {
                presenter.getHeaterDeviceMacAddress();
            } else {
                startActivity(DispenserActivity.class);
            }
        });
        availabilityDialog.show();
    }

    @Override
    public void gotoDevice(Class clz) {
        if (TextUtils.isEmpty(presenter.getToken())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(clz);
        }
    }

    @Override
    public void gotoDevice(Device device, String macAddress, String location, Long residenceId) {
        if (TextUtils.isEmpty(macAddress)) {
            onError("设备macAddress不合法");
        } else {
            Intent intent = new Intent(this, device.getClz());
            intent.putExtra(INTENT_KEY_LOCATION, location);
            intent.putExtra(INTENT_KEY_MAC_ADDRESS, macAddress);
            intent.putExtra(INTENT_KEY_DEVICE_TYPE, device.getType());
            intent.putExtra(INTENT_KEY_RESIDENCE_ID, residenceId);
            startActivity(intent);
        }
    }

    @Override
    public void showUrgentNotify(String content, Long id) {
        NoticeAlertDialog dialog = new NoticeAlertDialog(this);
        dialog.setContent(content);
        dialog.setOnOkClickListener(new NoticeAlertDialog.OnOkClickListener() {
            @Override
            public void onOkClick(Dialog dialog, boolean isNotReminder) {
                if (isNotReminder) {
                    presenter.readUrgentNotify(id);
                }
            }
        });
        dialog.show();
    }

    @Override
    public void refreshNoticeAmount() {
        presenter.getNoticeAmount();
    }

    @Override
    public void showBanners(List<String> banners) {
        hasBanners = true;
        EventBus.getDefault().post(new HomeFragment2.Event(HomeFragment2.Event.EventType.BANNER,
                banners));
    }

    @Override
    public void showSchoolBiz(List<BriefSchoolBusiness> businesses) {
        this.businesses = businesses;
        EventBus.getDefault().post(new HomeFragment2.Event(HomeFragment2.Event.EventType.SCHOOL_BIZ,
                businesses));
    }


    @Override
    public void showPrepayOrder(List<Order> orders) {
        int currentHeaterOrderSize = 0;
        for (Order order : orders) {
            currentHeaterOrderSize += CommonUtil.equals(order.getDeviceType(), 1) ? 1 : 0;
        }
        if (currentHeaterOrderSize != heaterOrderSize) {
            heaterOrderSize = currentHeaterOrderSize;
            HomeAdaptor.ItemWrapper itemWrapper = new HomeAdaptor.ItemWrapper(1, null, null, null, null,
                            Device.HEARTER.getDrawableRes());
            itemWrapper.setPrepaySize(heaterOrderSize);
            EventBus.getDefault().post(new HomeFragment2.Event(HomeFragment2.Event.EventType.PREPAY_ORDER,
                    itemWrapper));
        }
        int currentDispenserOrderSize = 0;
        for (Order order : orders) {
            dispenserOrderSize += CommonUtil.equals(order.getDeviceType(), 2) ? 1 : 0;
        }
        if (dispenserOrderSize != currentDispenserOrderSize) {
            dispenserOrderSize = currentDispenserOrderSize;
            HomeAdaptor.ItemWrapper itemWrapper = new HomeAdaptor.ItemWrapper(1, null, null, null, null,
                    Device.DISPENSER.getDrawableRes());
            itemWrapper.setPrepaySize(dispenserOrderSize);
            EventBus.getDefault().post(new HomeFragment2.Event(HomeFragment2.Event.EventType.PREPAY_ORDER,
                    itemWrapper));
        }
    }

    @Override
    public void showDeviceUsageDialog(int type, DeviceCheckRespDTO data) {
        if (data.getExistsUnsettledOrder() != null && data.getExistsUnsettledOrder()) {
            // 1 表示热水澡 2 表示饮水机
            if (type == 1) {
                // 直接前往热水澡处理找零
                gotoDevice(HEARTER, data.getMacAddress(), data.getLocation(), data.getResidenceId());
            } else {
                gotoDevice(DISPENSER, data.getMacAddress(), data.getLocation(), data.getResidenceId());
            }
        } else {
            if (type == 1 && heaterOrderSize > 0) {
                showPrepayDialog(type, heaterOrderSize, data);
            } else if (type == 2 && dispenserOrderSize > 0) {
                showPrepayDialog(type, dispenserOrderSize, data);
            } else {
                if (!data.getTimeValid()) {
                    showTimeValidDialog(data.getTitle(), data.getRemark(), type);
                } else {
                    if (type == 1) {
                        presenter.getHeaterDeviceMacAddress();
                    } else {
                        startActivity(new Intent(getApplicationContext(), ChooseDispenserActivity.class));
                    }
                }
            }
        }
    }

    public void showPrepayDialog(int type, int prepaySize, DeviceCheckRespDTO data) {
        if (prepayDialog == null) {
            prepayDialog = new PrepayDialog(this);
        }
        if (prepayDialog.isShowing()) {
            return;
        }
        prepayDialog.setDeviceTypeAndPrepaySize(type, prepaySize);
        prepayDialog.setOnOkClickListener(new PrepayDialog.OnOkClickListener() {
            @Override
            public void onOkClick(Dialog dialog) {
                startActivity(new Intent(getApplicationContext(), PrepayActivity.class));
            }
        });
        prepayDialog.setOnCancelClickListener(new PrepayDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick(Dialog dialog) {
                if (data.getTimeValid()) {
                    // 1 表示热水澡 2 表示饮水机
                    if (type == 1) {
                        presenter.getHeaterDeviceMacAddress();
                    } else {
                        gotoDevice(DispenserActivity.class);
                    }
                } else {
                    showTimeValidDialog(data.getTitle(), data.getRemark(), type);
                }
            }
        });
        prepayDialog.show();
    }

    @Override
    public void startActivity(AppCompatActivity activity, Class<?> clazz) {
        if (TextUtils.isEmpty(presenter.getToken())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            super.startActivity(activity, clazz);
        }
    }

    public void startActivity(Class<?> clz) {
        startActivity(this, clz);
    }

    public void checkTimeValid(Device device, Class clz) {
        if (TextUtils.isEmpty(presenter.getToken())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            presenter.queryTimeValid(device.getType(), clz);
        }
    }

    // 设备用水检查
    public void checkDeviceUsage(Device device) {
        if (TextUtils.isEmpty(presenter.getToken())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            presenter.checkDeviceUsage(device.getType());
        }
    }

    /**
     * 点击进入热水澡界面
     */
    public void gotoHeater() {
//        setBleCallback(() -> checkTimeValid(HEARTER, HeaterActivity.class));
        setBleCallback(() -> checkDeviceUsage(HEARTER));
        getBlePermission();
    }

    /**
     * 点击进入饮水机页面
     */
    public void gotoDispenser() {
//        setBleCallback(() -> checkTimeValid(DISPENSER, DispenserActivity.class));
        setBleCallback(() -> checkDeviceUsage(DISPENSER));
        getBlePermission();
    }

    /**
     * 点击进入失物招领
     */
    public void gotoLostAndFound() {
        startActivity(LostAndFoundActivity.class);
    }

    public void logout() {
        presenter.logout();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        switch (event.getType()) {
            case GOTO_HEATER:
                gotoHeater();
                break;
            case GOTO_DISPENSER:
                gotoDispenser();
                break;
            case GOTO_LOST_AND_FOUND:
                gotoLostAndFound();
                break;
            case START_ACTIVITY:
                startActivity((Class) event.getObject());
                break;
        }
    }

    @Data
    public static class Event {
        EventType type;
        Object object;

        public Event(EventType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public Event(EventType type) {
            this.type = type;
        }

        public enum EventType {
            GOTO_HEATER(1),
            GOTO_DISPENSER(2),
            GOTO_LOST_AND_FOUND(3),
            START_ACTIVITY(4)
            ;
            private int type;
            EventType(int type) {
                this.type = type;
            }
        }
    }

}
