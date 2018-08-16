package com.xiaolian.amigo.ui.device.bathroom;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.network.model.bathroom.BathBookingRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathOrderPreconditionRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BookingQueueProgressDTO;
import com.xiaolian.amigo.ui.device.bathroom.adapter.DeviceInfoAdapter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IBookingPresenter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IBookingView;
import com.xiaolian.amigo.ui.main.MainActivity;
import com.xiaolian.amigo.ui.widget.BathroomOperationStatusView;
import com.xiaolian.amigo.ui.widget.dialog.BathroomBookingDialog;
import com.xiaolian.amigo.ui.widget.dialog.BookingCancelDialog;
import com.xiaolian.amigo.ui.widget.dialog.PrepayDialog;
import com.xiaolian.amigo.util.Constant;
import com.xiaolian.amigo.util.DimentionUtils;
import com.xiaolian.amigo.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_DEVICE_NO;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomConstant.KEY_ORDER_PRECONDITION;
import static com.xiaolian.amigo.ui.device.bathroom.BathroomHeaterActivity.KEY_BATH_ORDER_ID;
import static com.xiaolian.amigo.ui.device.bathroom.ChooseBathroomActivity.KEY_BOOKING_RESULTCODE;
import static com.xiaolian.amigo.ui.widget.BathroomOperationStatusView.IMG_RES_STATUS_CANCEL;
import static com.xiaolian.amigo.ui.widget.BathroomOperationStatusView.IMG_RES_STATUS_FAIL;
import static com.xiaolian.amigo.ui.widget.BathroomOperationStatusView.IMG_RES_STATUS_OPERATING;
import static com.xiaolian.amigo.ui.widget.BathroomOperationStatusView.IMG_RES_STATUS_SUCCESS;

/**
 * 预约使用
 * @author zcd
 * @date 18/6/29
 */
public class BookingActivity extends UseWayActivity implements IBookingView {

    public  static final String KEY_BOOKING_ID = "BOOKING_ID" ;  //  预约id
    public  static final String KEY_BATHQUEUE_ID = "BATHQUEUE_ID" ; // 排队id

    public static final String KEY_DEVICE_ACTIVITY_FOR_RESULT = "KEY_DEVICE_ACTIVITY_FOR_RESULT" ;

    public static final int QUEUE_CANCEL =  1 ;   //  排队取消
    public static final int BOOKING_CANCEL = 2 ;   //  预约取消
    private static final String TAG = BookingActivity.class.getSimpleName();
    private String deviceNo="" ;
    private long orderId ;
    private BookingCancelDialog dialog ;

    /**
     *   取消排队预约
     */
    private BookingCancelDialog cancelQueueDialog ;
    @Inject
    IBookingPresenter<IBookingView> presenter;

    private boolean isTimeOut = false ;   //  是否超时


    private long bookingId ;   //  预约id
    private long bathQueueId ;   //  排队id

    private DeviceInfoAdapter.DeviceInfoWrapper wrapper = null ;  // 剩余时间
    private BathroomBookingDialog bathroomBookingDialog;
    private BathOrderPreconditionRespDTO bathOrderPreconditionRespDTO ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        setButtonText();
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData(){
        if (bookingId > 0){
            presenter.query(bookingId+"" , false ,0 , true);
        }else{
            if (bathQueueId > 0){
                presenter.queryQueueId(bathQueueId , false);
            }
        }
    }






    /**
     * 初始化失约提示,有失约记录时才显示失约提示
     */
    private void initTopTip(){
        if (bookingId > 0 &&missedTimes >0  ){
            if (tvBookingTip != null) setTopTip();
        }else{
            if (tvBookingTip != null) tvBookingTip.setVisibility(GONE);
        }
    }



    /**
     * 刷新RecyclerView
     */
    private void referItems(List<DeviceInfoAdapter.DeviceInfoWrapper> wrapperList , boolean isNeedChange){
        items.clear();
        items.addAll(wrapperList);
        if (isNeedChange) adapter.notifyDataSetChanged();
    }

    /**
     * 从未完成订单跳转的item界面
     * @Param isShowRemainTime  是否显示剩余时间
     * @return
     */
    private List<DeviceInfoAdapter.DeviceInfoWrapper> getListDevceInfoAdapter(BathBookingRespDTO data ,boolean isShowRemainTime){
        List<DeviceInfoAdapter.DeviceInfoWrapper> wrapperList = new ArrayList<>();
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("浴室位置：",
                data.getLocation(), R.color.colorDark2, 14, Typeface.NORMAL, false));
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("有效时间：" , getReservedTime(data.getCreateTime() ,data.getExpiredTime())
                ,R.color.colorDark2 ,14 , Typeface.NORMAL , false));
        if (isShowRemainTime) {
            wrapper  = new DeviceInfoAdapter.DeviceInfoWrapper("剩余时间：", TimeUtils.orderBathroomLastTime(data.getExpiredTime(), " "),
                    R.color.colorFullRed, 14, Typeface.NORMAL, false) ;
            wrapperList.add(wrapper);
        }
        return wrapperList ;
    }

    /**
     * 购买成功UI处理
     */
    private void buySuccessUi() {
        statusView.setStatusText("预约成功");
        llBottomVisible(false);
        statusView.setLeftImageResource(IMG_RES_STATUS_SUCCESS);
        statusView.showCancelButton(new BathroomOperationStatusView.OnCancelClickListener(){

            @Override
            public void onCancelClick() {
                presenter.cancel(orderId);
                presenter.cancelCountDown();
            }
        });
        initTopTip();
        btStartToUse.setVisibility(View.GONE);
    }


    @Override
    protected void initIntent() {
        super.initIntent();
        if (getIntent() != null){
            deviceNo = getIntent().getStringExtra(KEY_DEVICE_NO );
            bathOrderPreconditionRespDTO = getIntent().getParcelableExtra(KEY_ORDER_PRECONDITION);

            //

            bookingId = getIntent().getLongExtra(KEY_BOOKING_ID , 0);
            bathQueueId = getIntent().getLongExtra(KEY_BATHQUEUE_ID , 0);

        }
    }

    private void setTopTip() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString tipSpan1 = new SpannableString("指定浴室每月失约" + maxMissAbleTimes
                + "次后将无法预约，已");
        tipSpan1.setSpan(new AbsoluteSizeSpan(
                        DimentionUtils.convertSpToPixels(12, this)), 0, tipSpan1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tipSpan1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorDark6)),
                0, tipSpan1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(tipSpan1);
        SpannableString missedTimesSpan = new SpannableString(String.valueOf(missedTimes));
        missedTimesSpan.setSpan(new AbsoluteSizeSpan(
                        DimentionUtils.convertSpToPixels(12, this)), 0, missedTimesSpan.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        missedTimesSpan.setSpan(new ForegroundColorSpan(getResources().getColor( R.color.colorFullRed)),
                0, missedTimesSpan.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(missedTimesSpan);
        SpannableString tipSpan2 = new SpannableString("次");
        tipSpan2.setSpan(new AbsoluteSizeSpan(
                        DimentionUtils.convertSpToPixels(12, this)), 0, tipSpan2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tipSpan2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDark6)),
                0, tipSpan2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(tipSpan2);
        if (tvBookingTip != null) {
            tvBookingTip.setText(builder);
            tvBookingTip.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void setToolbarTitle(TextView textView) {
        textView.setText("预约使用");
    }

    @Override
    protected void setTitle(TextView textView) {
        textView.setText("预约使用");
    }

    @Override
    protected void setToolbarSubTitle(TextView textView) {
        textView.setText("");
//        textView.setOnClickListener(v -> onSubtitleClick());
    }

    @Override
    protected void setSubTitle(TextView textView) {
        textView.setText("");
//        textView.setOnClickListener(v -> onSubtitleClick());
    }

    /**
     * 设置tips
     * @param type
     */
    public void setTip(int type){
        if (type == Constant.BOOKING_DEVICE){
            getTvTip1().setText(getString(R.string.booking_user_this));
        }else if (type == Constant.BOOKING_FLOOR){
            getTvTip1().setText(getString(R.string.booking_user_empty));
        }

        getTvTip2().setText(R.string.booking_user_common_tip);

        getTvTip().setText("预约使用说明");
    }

    /**
     * 设置排队预约tip
     */
    public void setQueueTip(){
        getTvTip1().setText(getString(R.string.booking_queue_tip_1));

        getTvTip2().setText(R.string.booking_queue_tip_2);

        getTvTip3().setText(R.string.booking_queue_tip_3);

        getTvTip().setText("预约使用说明");
    }

    @Override
    protected void setTips(TextView tip1, TextView tip2, TextView tip3, TextView tip4,
                           TextView tip, RelativeLayout rlTip) {
    }

    @Override
    protected String getButtonText() {
        return "确认预约";
    }

    private void showBookingDialog() {
        if (bathroomBookingDialog == null) {
            bathroomBookingDialog = new BathroomBookingDialog(this);
        }
        bathroomBookingDialog.show();
    }


    /**
     * 取消预约弹窗
     * @param id
     */
    private void cancelDialog(long id , int type){

        if (type == BOOKING_CANCEL ) {
            if (dialog == null) {
                dialog = new BookingCancelDialog(this);
                dialog.setTvTip(getString(R.string.book_dialog_tip));
                dialog.setTvTitle(getString(R.string.book_dialog_title));
                dialog.setOnCancelClickListener(new PrepayDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick(Dialog dialog) {
                        presenter.cancel(id);
                    }
                });
                dialog.setOnOkClickListener(new PrepayDialog.OnOkClickListener() {
                    @Override
                    public void onOkClick(Dialog dialog) {
                        dialog.cancel();
                    }
                });
            }
            dialog.show();
        }else if (type == QUEUE_CANCEL){
            if (cancelQueueDialog == null) {
                cancelQueueDialog = new BookingCancelDialog(this);
                cancelQueueDialog.setTvTip(getString(R.string.queue_dialog_tip));
                cancelQueueDialog.setTvTitle(getString(R.string.queue_dialog_title));
                cancelQueueDialog.setOnCancelClickListener(new PrepayDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick(Dialog dialog) {
                        presenter.cancelQueue(id);
                    }
                });
                cancelQueueDialog.setOnOkClickListener(new PrepayDialog.OnOkClickListener() {
                    @Override
                    public void onOkClick(Dialog dialog) {
                        dialog.cancel();
                    }
                });
            }
            cancelQueueDialog.show();
        }
    }
    /**
     * 预约成功界面刷新
     * @param data
     */
    private void successRecyclerView(BathBookingRespDTO data){
        List<DeviceInfoAdapter.DeviceInfoWrapper> wrapperList = new ArrayList<>();
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("浴室位置：",
                data.getLocation(), R.color.colorDark2, 14, Typeface.NORMAL, false));
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("预留时间：" , getReservedTime(data.getCreateTime() ,data.getExpiredTime())
                ,R.color.colorDark2 ,14 , Typeface.NORMAL , false));
        wrapper = new DeviceInfoAdapter.DeviceInfoWrapper("剩余时间：" ,TimeUtils.orderBathroomLastTime(data.getExpiredTime() ," "),
                R.color.colorFullRed , 14 , Typeface.NORMAL , false);
        wrapperList.add(wrapper);
        referRecyclerView(wrapperList);
    }

    /**
     * 刷新recyclerView 界面
     * @param
     */
    private void referRecyclerView( List<DeviceInfoAdapter.DeviceInfoWrapper> wrapperList) {
        items.clear();
        items.addAll(wrapperList);
        adapter.notifyDataSetChanged();
    }

    private String getReservedTime(long startTime , long endTime){
        return TimeUtils.covertTimeToString(startTime) +"~"+TimeUtils.covertTimeToString(endTime);
    }

    @Override
    public void bookingSuccess(BathBookingRespDTO data) {
        isTimeOut = false ;
        setTip(data.getType());
        deviceNo = data.getDeviceNo()+"" ;
        statusView.setLeftImageResource(IMG_RES_STATUS_SUCCESS);
        statusView.setStatusText("预约成功");
        btStartToUse.setVisibility(View.GONE);
        statusView.showCancelButton(() -> {
            cancelDialog(data.getId() , BOOKING_CANCEL);

        });
        successRecyclerView(data);
        statusView.getRightText().setText("取消");
        statusView.getRightText().setTextColor(getResources().getColor(R.color.colorDarkB ));
        presenter.countDownexpiredTime(data.getExpiredTime());
        presenter.query(data.getId()+"" ,true , 10 , true);
    }


    @Override
    public void bookingCancel() {
        isTimeOut = true ;
        statusView.setLeftImageResource(IMG_RES_STATUS_CANCEL);
        statusView.setStatusText("取消预约");
        statusView.hideCancelButton();
        CancelBookingView();
    }

    /**
     * 取消预约
     */
    private void CancelBookingView(){
        if (items!= null && items.size() > 0 && adapter != null){
            items.remove(items.size() -1 );
            adapter.notifyDataSetChanged();
            presenter.cancelCountDown();
        }
        cancelBookingUi();

    }

    /**
     * 取消预约的处理
     */
    private void cancelBookingUi() {
        btStartToUse.setVisibility(View.GONE);
        llBottomVisible(true);
        rightOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(KEY_DEVICE_ACTIVITY_FOR_RESULT ,deviceNo);
                BookingActivity.this.setResult(KEY_BOOKING_RESULTCODE,intent);
                BookingActivity.this.finish();
            }
        });
    }



    @Override
    public void finishActivity() {
        this.finish();
    }


    @Override
    public void appointMentTimeOut(BathBookingRespDTO respDTO) {
        isTimeOut = true  ;
        missedTimes++  ;
        setTopTip();
        deviceNo = respDTO.getDeviceNo()+"" ;
        statusView.setLeftImageResource(IMG_RES_STATUS_FAIL);
        statusView.setStatusText(getString(R.string.preBookTimeOut));
        statusView.getTip().setText(getString(R.string.tip_timeout));
        statusView.hideCancelButton();
        btStartToUse.setVisibility(GONE);
        cancelBookingUi();
        List<DeviceInfoAdapter.DeviceInfoWrapper> deviceInfoWrappers = getListDevceInfoAdapter(respDTO , true);
        referItems(deviceInfoWrappers , true);
    }

    @Override
    public void appointMentTimeOut(boolean isServer) {
        if (!isServer) presenter.notifyExpired();
        isTimeOut = true ;
        missedTimes++  ;
        setTopTip();
        statusView.setLeftImageResource(IMG_RES_STATUS_FAIL);
        statusView.setStatusText(getString(R.string.preBookTimeOut));
        statusView.getTip().setText(getString(R.string.tip_timeout));
        statusView.hideCancelButton();
        btStartToUse.setVisibility(GONE);
        cancelBookingUi();
    }



    @Override
    public void showQueue(BookingQueueProgressDTO data) {
        initTopTip();
        getQueueInfo(data);
        setQueueTip();

    }




    /**
     * 刷新预约排队的信息
     * @param data
     */
    public void getQueueInfo(BookingQueueProgressDTO data ){
        statusView.setStatusText(getString(R.string.preBookingWait));
        statusView.setLeftImageResource(IMG_RES_STATUS_OPERATING);
        statusView.setTip(getString(R.string.preBookingWaitTip));
        statusView.showCancelButton(new BathroomOperationStatusView.OnCancelClickListener(){

            @Override
            public void onCancelClick() {
                cancelDialog(data.getId() , QUEUE_CANCEL);
            }
        });
        llBottomVisible(false);
        List<DeviceInfoAdapter.DeviceInfoWrapper> list = getListWaitItems(data.getLocation() , data.getRemain());
        referItems(list , true);
    }


    public List<DeviceInfoAdapter.DeviceInfoWrapper> getListWaitItems(String location , int remain){
        List<DeviceInfoAdapter.DeviceInfoWrapper> wrapperList = new ArrayList<>();
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("浴室位置" ,
                location , R.color.colorDark2 , 14 ,Typeface.NORMAL , false));
        wrapperList.add(new DeviceInfoAdapter.DeviceInfoWrapper("还需等待",
                remain +"人" , R.color.refresh_red ,14 , Typeface.NORMAL , false));
        return wrapperList ;
    }

    @Override
    public void showWaitLoading() {
        showBookingDialog();
    }

    @Override
    public void hideWaitLoading() {
        if (bathroomBookingDialog != null && bathroomBookingDialog.isShowing()){
            bathroomBookingDialog.dismiss();
        }
    }

    @Deprecated
    @Override
    public void SendDeviceTimeOut() {

    }

    @Override
    public void countTimeLeft(String text) {
        if (wrapper != null && items != null && items.size() > 0 && adapter != null){
            int currentPosition = items.indexOf(wrapper);
            if (currentPosition != -1){
                wrapper.setRightText(text);
                items.set(currentPosition ,wrapper);
                adapter.notifyItemChanged(currentPosition);
            }
        }
    }

    @Override
    public void gotoUsing(long bathOrderId) {
        isTimeOut = false ;
        presenter.cancelCountDown();
        startActivity(new Intent(this ,BathroomHeaterActivity.class)
        .putExtra(KEY_BATH_ORDER_ID , bathOrderId));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bathroomBookingDialog != null){
            bathroomBookingDialog.onDettechView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isTimeOut){
            startActivity(new Intent(this , MainActivity.class));
        }else{
            finishActivity();
        }
    }
}