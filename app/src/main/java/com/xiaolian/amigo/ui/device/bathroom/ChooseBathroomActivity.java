package com.xiaolian.amigo.ui.device.bathroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ObjectsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.network.model.bathroom.BathBuildingRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathOrderCurrentRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathOrderPreconditionRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BuildingTrafficDTO;
import com.xiaolian.amigo.ui.base.WebActivity;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomPresenter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomView;
import com.xiaolian.amigo.ui.main.MainActivity;
import com.xiaolian.amigo.ui.user.EditDormitoryActivity;
import com.xiaolian.amigo.ui.widget.AutoBathroom;
import com.xiaolian.amigo.ui.widget.dialog.BathroomBookingDialog;
import com.xiaolian.amigo.ui.widget.popWindow.ChooseBathroomPop;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xiaolian.amigo.ui.device.DeviceOrderActivity.KEY_USER_STYLE;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_BALANCE;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_MAX_MISSABLE_TIMES;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_MIN_PREPAY;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_MISSED_TIMES;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_PREPAY;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomHeaterActivity.KEY_BATH_ORDER_ID;
import static com.xiaolian.amigo.ui.device.bathroom.BookingActivity.KEY_BATHQUEUE_ID;
import static com.xiaolian.amigo.ui.device.bathroom.BookingActivity.KEY_BOOKING_ID;
import static com.xiaolian.amigo.ui.device.bathroom.BookingActivity.KEY_DEVICE_ACTIVITY_FOR_RESULT;
import com.xiaolian.amigo.data.network.model.bathroom.BathBuildingRespDTO.FloorsBean.GroupsBean;

/**
 * 选择浴室
 *
 * @author zcd
 * @date 18/6/27
 */
public class ChooseBathroomActivity extends BathroomBaseActivity implements IChooseBathroomView , AutoBathroom.BathroomClick {
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_RESIDENCE_ID = "key_residence_id";  // 建筑id
    public static final String KEY_RESIDENCE_TYPE = "key_residence_type";
    public static final String KEY_BUILDING_ID = "key_building_id";
    public static final String KEY_RESIDENCE_NAME = "key_residence_name";
    public static final int KEY_BOOKING_RESQCODE = 11;   //  booking 页面返回

    public static final int KEY_BOOKING_RESULTCODE = 12;  //
    private static final String TAG = ChooseBathroomActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_help)
    ImageView ivHelp;
    @BindView(R.id.pre_bathroom)
    Button preBathroom;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.auto_bathroom)
    AutoBathroom autoBathroom;
    private BathroomBookingDialog bathroomBookingDialog;

    private long buildId;    //  楼栋id ；
    private long residenceId;   //  建筑id
    private long residenceType;
    private long id;
    private String residenceName;


    private boolean isShowDialog = true;  //  是否显示动画dialog
    /**
     * 预付金额
     */
    protected Double prepay;
    /**
     * 最小预付金额
     */
    protected Double minPrepay;
    /**
     * 用户余额
     */
    protected Double balance;
    /**
     * 失约次数 只有预约才会返回
     */
    protected Integer missedTimes;
    /**
     * 总共可失约次数 只有预约才会返回
     */
    protected Integer maxMissAbleTimes;


    @Inject
    IChooseBathroomPresenter<IChooseBathroomView> presenter;


    private String deviceName;

//    private List<ChooseBathroomOuterAdapter.BathGroupWrapper> bathGroups = new ArrayList<>();


    //    private ZoomRecyclerView recyclerView;
//    private ChooseBathroomOuterAdapter outerAdapter;
    private int lastSelectedGroupPosition = -1;
    private int lastSelectedRoomPosition = -1;
    private String deviceNo = "";


    /**
     * 当前是否处于选中状态 选中状态显示预约使用 非选中状态显示购买编码
     */
    private boolean isSelected = false;


    /**
     * 显示选择房间信息的popwindow
     */
    private ChooseBathroomPop pop;

    private List<BathBuildingRespDTO.FloorsBean> floorsBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bathroom);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        setUp();
        bindView();
        initRecyclerView();
        initPop();


    }


    public void initPop() {
        if (pop == null) {
            pop = new ChooseBathroomPop(this);
            pop.setPopButtonClickListener(new ChooseBathroomPop.PopButtonClickListener() {
                @Override
                public void click(BuildingTrafficDTO.FloorsBean floorsBean) {
                    if (!TextUtils.isEmpty(floorsBean.getDeviceNo())) {
                        presenter.booking(Long.parseLong(floorsBean.getDeviceNo()), 0);
                    } else {
                        presenter.booking(0, floorsBean.getId());
                    }
                }
            });
        }
    }


    private void bindView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());
//        recyclerView = findViewById(R.id.recyclerView);
        setBtnText("预约洗澡 (任意空浴室)", false);
        isShowDialog = true;
        autoBathroom.setBathroomClick(this);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * 获取帮助
     */
    private void help() {
       startActivity( new Intent(getApplicationContext(), WebActivity.class)
                .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_HELP));
    }

    @OnClick({R.id.iv_help, R.id.tv_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_help:
                help();
                break;
            case R.id.tv_title:
                startEditBathroom();
                break;
        }
    }


    /**
     * 选择洗澡地址
     */
    private void startEditBathroom() {
        startActivity(new Intent(this, EditDormitoryActivity.class));
    }

    private void gotoShowerAddress() {
        startActivity(new Intent(this, ShowerAddressActivity.class));
    }

    private void initRecyclerView() {

//        test();
        presenter.getBathroomList(buildId);
        showBathroomDialog();
    }

    private void test() {
        List<GroupsBean> groupsBeans = new ArrayList<>();

        List<GroupsBean.BathRoomsBean> bathRoomsBeans = new ArrayList<>();

        List<GroupsBean.BathRoomsBean> bathRoomsBeans1 = new ArrayList<>();

        GroupsBean.BathRoomsBean bathRoomsBean1 = new GroupsBean.BathRoomsBean();
        bathRoomsBean1.setId(11111);
        bathRoomsBean1.setXaxis(1);
        bathRoomsBean1.setYaxis(1);
        bathRoomsBean1.setStatus(2);
        bathRoomsBeans.add(bathRoomsBean1);

        GroupsBean.BathRoomsBean bathRoomsBean2 = new GroupsBean.BathRoomsBean();
        bathRoomsBean2.setId(2222);
        bathRoomsBean2.setXaxis(1);
        bathRoomsBean2.setYaxis(2);
        bathRoomsBean2.setStatus(2);
        bathRoomsBeans.add(bathRoomsBean2);

        GroupsBean.BathRoomsBean bathRoomsBean7 = new GroupsBean.BathRoomsBean();
        bathRoomsBean7.setId(2222);
        bathRoomsBean7.setXaxis(1);
        bathRoomsBean7.setYaxis(2);
        bathRoomsBean7.setStatus(1);
        bathRoomsBeans.add(bathRoomsBean7);

        GroupsBean.BathRoomsBean bathRoomsBean4 = new GroupsBean.BathRoomsBean();
        bathRoomsBean4.setId(33333);
        bathRoomsBean4.setXaxis(2);
        bathRoomsBean4.setYaxis(2);
        bathRoomsBean4.setStatus(1);
        bathRoomsBeans.add(bathRoomsBean4);


        GroupsBean.BathRoomsBean bathRoomsBean3 = new GroupsBean.BathRoomsBean();
        bathRoomsBean3.setId(2222);
        bathRoomsBean3.setXaxis(1);
        bathRoomsBean3.setYaxis(1);
        bathRoomsBean3.setStatus(2);
        bathRoomsBeans1.add(bathRoomsBean3);

        GroupsBean.BathRoomsBean bathRoomsBean5 = new GroupsBean.BathRoomsBean();
        bathRoomsBean5.setId(2222);
        bathRoomsBean5.setXaxis(1);
        bathRoomsBean5.setYaxis(5);
        bathRoomsBean5.setStatus(1);
        bathRoomsBeans1.add(bathRoomsBean5);

        GroupsBean.BathRoomsBean bathRoomsBean6 = new GroupsBean.BathRoomsBean();
        bathRoomsBean6.setId(2222);
        bathRoomsBean6.setXaxis(5);
        bathRoomsBean6.setYaxis(2);
        bathRoomsBean6.setStatus(4);
        bathRoomsBeans1.add(bathRoomsBean6);


        GroupsBean groupsBean = new GroupsBean();
        groupsBean.setBathRooms(bathRoomsBeans);
        groupsBean.setDisplayName("A层1组101房间");
        groupsBeans.add(groupsBean);


        GroupsBean groupsBean1 = new GroupsBean();
        groupsBean1.setBathRooms(bathRoomsBeans1);
        groupsBean1.setDisplayName("A层2组102房间");
        groupsBeans.add(groupsBean1);


        BathBuildingRespDTO.FloorsBean floorsBean2 = new BathBuildingRespDTO.FloorsBean();
        floorsBean2.setGroups(groupsBeans);
        floorsBeans.add(floorsBean2);
        autoBathroom.setData(floorsBeans);
    }


    /**
     * 显示有设备提示的弹窗
     *
     * @param deviceName
     * @param deviceNo
     */
    private void showPop(String deviceName, String deviceNo) {
        BuildingTrafficDTO.FloorsBean floorsBean = new BuildingTrafficDTO.FloorsBean();
        floorsBean.setName(deviceName);
        floorsBean.setDeviceNo(deviceNo);

        //  避免预约指定房间出现空闲几人的显示
        floorsBean.setWaitCount(-1);
        floorsBean.setAvailableCount(-1);
        showPopForDevice(floorsBean);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        presenter.precondition(isShowDialog);
//        presenter.buildingTraffic(buildId);
    }

    @Override
    protected void setUp() {
        super.setUp();
        if (getIntent() != null) {
            buildId = getIntent().getLongExtra(KEY_BUILDING_ID, -1);
            residenceId = getIntent().getLongExtra(KEY_RESIDENCE_ID, -1);
            residenceType = getIntent().getLongExtra(KEY_RESIDENCE_TYPE, -1);
            id = getIntent().getLongExtra(KEY_ID, -1);
            residenceName = getIntent().getStringExtra(KEY_RESIDENCE_NAME);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
        if (bathroomBookingDialog != null) {
            bathroomBookingDialog.onDettechView();
        }
        presenter.clearTime();
    }


    private void showPopForDevice(BuildingTrafficDTO.FloorsBean floorsBean) {
        if (pop == null) {
            pop = new ChooseBathroomPop(this);
            pop.setPopButtonClickListener(new ChooseBathroomPop.PopButtonClickListener() {
                @Override
                public void click(BuildingTrafficDTO.FloorsBean floorsBean) {
                    if (!TextUtils.isEmpty(floorsBean.getDeviceNo())) {
                        presenter.booking(Long.parseLong(floorsBean.getDeviceNo()), 0);
                    } else {
                        presenter.booking(0, floorsBean.getId());
                    }
                }
            });
        }

        pop.setData(floorsBean);
        showPopNoDevice();
    }


    /**
     * 显示pop
     */
    private void showPop() {
        presenter.buildingTraffic(buildId);
        if (pop == null) {
            pop = new ChooseBathroomPop(this);
            pop.setPopButtonClickListener(new ChooseBathroomPop.PopButtonClickListener() {
                @Override
                public void click(BuildingTrafficDTO.FloorsBean floorsBean) {
                    if (!TextUtils.isEmpty(floorsBean.getDeviceNo())) {
                        presenter.booking(Long.parseLong(floorsBean.getDeviceNo()), 0);
                    } else {
                        presenter.booking(0, floorsBean.getId());
                    }
                }
            });
        }
        showPopNoDevice();
    }

    private void showPopNoDevice() {
        pop.showUp(root);
        lightOff();
        /**
         * 消失时屏幕变亮
         */
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

                layoutParams.alpha = 1.0f;

                getWindow().setAttributes(layoutParams);
            }
        });
    }


    /**
     * 显示时屏幕变暗
     */
    private void lightOff() {

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        layoutParams.alpha = 0.6f;

        getWindow().setAttributes(layoutParams);

    }

    

    @Override
    public void refreshBathroom(List<ChooseBathroomOuterAdapter.BathGroupWrapper> wrappers,
                                List<Integer> methods, Integer missTimes) {
//        bathGroups.clear();
//        bathGroups.addAll(wrappers);
//        outerAdapter.notifyDataSetChanged();
//        for (Integer method : methods) {
//            setBathroomMethod(method);
//        }
    }

    @Override
    public void refreshBathroom(BathBuildingRespDTO respDTO) {
        if (floorsBeans == null) {
            floorsBeans = respDTO.getFloors();
        } else {
            floorsBeans.clear();
            floorsBeans.addAll(respDTO.getFloors());
        }

        autoBathroom.setData(floorsBeans);
    }


    public void setBathroomMethod(int methods) {

//        if (methods == BOOKING.getCode()) {
//            metaBall.getLlLeft().setVisibility(View.VISIBLE);
//        } else if (methods == BUY_CODE.getCode()) {
//            metaBall.getLlRight().setVisibility(View.VISIBLE);
//        }

    }


    @Override
    public void showBathroomDialog() {
        if (bathroomBookingDialog == null) {
            bathroomBookingDialog = new BathroomBookingDialog(this);
        }
        bathroomBookingDialog.show();
    }


    @Override
    public void hideBathroomDialog() {
        if (bathroomBookingDialog != null) {
            bathroomBookingDialog.cancel();
        }
    }

    @Override
    public void setTvTitle(String name) {
        tvTitle.setText(name);
    }


    @Override
    public void setBtnText(String text, boolean isSelected) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new AbsoluteSizeSpan(16, true), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(14, true), 4, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        preBathroom.setText(spannable);
        this.isSelected = isSelected;
        if (isSelected) preBathroom.setBackgroundResource(R.drawable.green_button);
        else preBathroom.setBackgroundResource(R.drawable.red_button);
    }

    @Override
    public void trafficInfo(List<BuildingTrafficDTO.FloorsBean> floorsBeans) {
        pop.setData(floorsBeans);
    }

    @Override
    public void startQueue(long id) {
        startActivityForResult(new Intent(this, BookingActivity.class)
                .putExtra(KEY_BATHQUEUE_ID, id)
                .putExtra(KEY_BALANCE, balance)
                .putExtra(KEY_MIN_PREPAY, minPrepay)
                .putExtra(KEY_PREPAY, prepay)
                .putExtra(KEY_MISSED_TIMES, missedTimes)
                .putExtra(KEY_MAX_MISSABLE_TIMES, maxMissAbleTimes), KEY_BOOKING_RESQCODE);

    }

    @Override
    public void startBooking(long bathOrderId) {
        startActivityForResult(new Intent(this, BookingActivity.class)
                .putExtra(KEY_BOOKING_ID, bathOrderId)
                .putExtra(KEY_BALANCE, balance)
                .putExtra(KEY_MIN_PREPAY, minPrepay)
                .putExtra(KEY_PREPAY, prepay)
                .putExtra(KEY_MISSED_TIMES, missedTimes)
                .putExtra(KEY_MAX_MISSABLE_TIMES, maxMissAbleTimes), KEY_BOOKING_RESQCODE
        );
    }


    @Override
    public void saveBookingInfo(BathOrderPreconditionRespDTO data) {
        balance = data.getPrepayInfo().getBalance();
        minPrepay = data.getPrepayInfo().getMinPrepay();
        prepay = data.getPrepayInfo().getPrepay();
        maxMissAbleTimes = data.getMaxMissAbleTimes();
        missedTimes = data.getMissedTimes();
    }

    @Override
    public void startUsing(long bathOrderId) {
        startActivity(new Intent(this, BathroomHeaterActivity.class)
                .putExtra(KEY_BATH_ORDER_ID, bathOrderId));
    }

    @Override
    public void startOrderInfo(BathOrderCurrentRespDTO data) {
        String userMethod = "";
        if (data.getLocation().equals("任意空浴室")) {
            userMethod = "预约任意空浴室";
        } else {
            userMethod = "预约指定浴室";
        }
        Intent intent = new Intent(this, BathOrderActivity.class);
        intent.putExtra(Constant.BUNDLE_ID, data.getBathOrderId());
        intent.putExtra(KEY_USER_STYLE, userMethod);
        startActivity(intent);
    }


    @OnClick(R.id.pre_bathroom)
    public void preBathRoom() {

        if (TextUtils.isEmpty(deviceNo)) {
            showPop();
        } else {
            autoShowDevice(deviceNo);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case KEY_BOOKING_RESQCODE:
                if (resultCode == KEY_BOOKING_RESULTCODE) {
                    autoChoseBathroom(data);
                }
                break;
        }
    }

    /**
     * 重新预约时，自动帮用户选择
     */
    private void autoChoseBathroom(Intent data) {
        isShowDialog = false;
        String deviceNo = data.getStringExtra(KEY_DEVICE_ACTIVITY_FOR_RESULT);
        if (TextUtils.isEmpty(deviceNo)) {
            showPop();
        } else {

            autoShowDevice(deviceNo);
//            showPopForDevice();
        }

    }

    private void autoShowDevice(String deviceNo) {
        if (floorsBeans!= null && floorsBeans.size() > 0){

             for (BathBuildingRespDTO.FloorsBean floorsBean  : floorsBeans){
                  for (GroupsBean groupsBean: floorsBean.getGroups()){

                      for (GroupsBean.BathRoomsBean bathRoomsBean : groupsBean.getBathRooms()) {
                          if (ObjectsCompat.equals(deviceNo, bathRoomsBean.getDeviceNo())) {
                              this.deviceNo = bathRoomsBean.getDeviceNo() +"" ;
                              showPop(bathRoomsBean.getName() , bathRoomsBean.getDeviceNo() +"");
                          }
                      }
                  }
             }
        }
    }

    @Override
    public void BathroomClick(BathBuildingRespDTO.FloorsBean.GroupsBean.BathRoomsBean bathRoomsBean) {
        showPop(bathRoomsBean.getName() , bathRoomsBean.getDeviceNo()+"");
        this.deviceNo = bathRoomsBean.getDeviceNo()+"" ;

    }

    @Override
    public void onScale(float scale) {

    }
}