package com.xiaolian.amigo.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.ComplaintType;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.network.model.order.Order;
import com.xiaolian.amigo.ui.base.WebActivity;
import com.xiaolian.amigo.ui.order.intf.IOrderDetailView;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消费账单详情
 * <p>
 * Created by caidong on 2017/9/18.
 */
public class OrderDetailActivity extends OrderBaseActivity implements IOrderDetailView {

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_device_location)
    TextView tv_device_location;
    @BindView(R.id.tv_order_no)
    TextView tv_order_no;
    @BindView(R.id.tv_pay_method)
    TextView tv_pay_method;
    @BindView(R.id.tv_change_amount)
    TextView tv_change_amount;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.iv_order_free)
    ImageView iv_order_free;
    @BindView(R.id.rl_odd)
    RelativeLayout rl_odd;

    private Order order;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        render();
    }

    @Override
    protected void setUp() {
        Intent intent = getIntent();
        this.order = (Order) intent.getSerializableExtra(Constant.EXTRA_KEY);
        this.token = intent.getStringExtra(Constant.TOKEN);
    }

    /**
     * 投诉
     */
    @OnClick(R.id.tv_complaint)
    public void complaint() {
        startActivity(new Intent(this, WebActivity.class)
                .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_COMPLAINT
                        + "?token=" + token
                        + "&orderId=" + order.getId()
                        + "&orderNo=" + order.getOrderNo()
                        + "&orderType="
                        + ComplaintType.getComplaintTypeByDeviceType(
                                Device.getDevice(order.getDeviceType())).getType()));
    }

    /**
     * 复制
     */
    @OnClick(R.id.tv_copy)
    public void copy() {
        CommonUtil.copy(tv_order_no.getText().toString(), getApplicationContext());
        onSuccess("复制成功");
    }

    @Override
    public void render() {
        tv_time.setText(CommonUtil.stampToDate(order.getCreateTime()));
        Device device = Device.getDevice(order.getDeviceType());
        if (device != null) {
            tv_device_location.setText(device.getDesc() + " " + order.getLocation());
        } else {
            tv_device_location.setText("未知设备 " + order.getLocation());
        }
        tv_order_no.setText(order.getOrderNo());
        tv_pay_method.setText(order.getPrepay());
        rl_odd.setVisibility(order.getPaymentType() == 1 ?
                View.VISIBLE : View.GONE);
        iv_order_free.setVisibility(order.getPaymentType() == 1 ?
                View.GONE : View.VISIBLE);
        tv_change_amount.setText(String.valueOf(order.getOdd()));
        tv_amount.setText(String.valueOf(order.getConsume()));
    }

}
