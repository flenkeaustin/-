package com.xiaolian.amigo.ui.device.dispenser;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.enumeration.DispenserWater;
import com.xiaolian.amigo.data.network.model.device.ScanDevice;
import com.xiaolian.amigo.data.network.model.device.ScanDeviceGroup;
import com.xiaolian.amigo.data.network.model.dto.response.OrderPreInfoDTO;
import com.xiaolian.amigo.ui.device.WaterDeviceBaseActivity;
import com.xiaolian.amigo.ui.device.intf.dispenser.IChooseDispenerView;
import com.xiaolian.amigo.ui.device.intf.dispenser.IChooseDispenserPresenter;
import com.xiaolian.amigo.ui.main.MainActivity;

import java.util.List;

import lombok.Data;

/**
 * 饮水机适配器
 * @author zcd
 */

public class ChooseDispenserAdaptor extends RecyclerView.Adapter<ChooseDispenserAdaptor.ViewHolder> {
    private int lastExpandPosition = -1;
    private List<ChooseDispenserAdaptor.DispenserWrapper> mData;
    private Context context;
    private int layoutId;
    private IChooseDispenserPresenter<IChooseDispenerView> presenter;
    private OrderPreInfoDTO orderPreInfo;

    public ChooseDispenserAdaptor(Context context, int layoutId,
                                  List<DispenserWrapper> datas ,
                                  IChooseDispenserPresenter<IChooseDispenerView> presenter,
                                  OrderPreInfoDTO orderPreInfo) {
        this.context = context;
        this.mData = datas;
        this.layoutId = layoutId;
        this.presenter = presenter;
        this.orderPreInfo = orderPreInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // 添加饮水机点击事件
        holder.tv_cold_water.setOnClickListener(v -> {
            presenter.closeBleConnection();
            DispenserWrapper dispenserWrapper = mData.get(holder.getAdapterPosition());
            context.startActivity(new Intent(context.getApplicationContext(), DispenserActivity.class)
                    .putExtra(MainActivity.INTENT_KEY_MAC_ADDRESS, dispenserWrapper.getCold().getMacAddress())
                    .putExtra(DispenserActivity.INTENT_KEY_FAVOR, dispenserWrapper.isFavor())
                    .putExtra(DispenserActivity.INTENT_KEY_ID, dispenserWrapper.getResidenceId())
                    .putExtra(DispenserActivity.INTENT_KEY_TEMPERATURE, DispenserWater.COLD.getType())
                    .putExtra(MainActivity.INTENT_KEY_LOCATION, dispenserWrapper.getLocation())
                    .putExtra(MainActivity.INTENT_KEY_DEVICE_TYPE, Device.DISPENSER.getType())
                    .putExtra(WaterDeviceBaseActivity.INTENT_PREPAY_INFO, orderPreInfo));
            if (presenter.getAction() == ChooseDispenserActivity.ACTION_CHANGE_DISPENSER) {
                presenter.finishView();
            }
        });
        // 冰水
        holder.tv_ice_water.setOnClickListener(v -> {
            presenter.closeBleConnection();
            DispenserWrapper dispenserWrapper = mData.get(holder.getAdapterPosition());
            context.startActivity(new Intent(context.getApplicationContext(), DispenserActivity.class)
                    .putExtra(MainActivity.INTENT_KEY_MAC_ADDRESS, dispenserWrapper.getIce().getMacAddress())
                    .putExtra(DispenserActivity.INTENT_KEY_FAVOR, dispenserWrapper.isFavor())
                    .putExtra(DispenserActivity.INTENT_KEY_ID, dispenserWrapper.getResidenceId())
                    .putExtra(DispenserActivity.INTENT_KEY_TEMPERATURE, DispenserWater.ICE.getType())
                    .putExtra(MainActivity.INTENT_KEY_LOCATION, dispenserWrapper.getLocation())
                    .putExtra(MainActivity.INTENT_KEY_DEVICE_TYPE, Device.DISPENSER.getType())
                    .putExtra(WaterDeviceBaseActivity.INTENT_PREPAY_INFO, orderPreInfo));
        });
        // 热水
        holder.tv_hot_water.setOnClickListener(v -> {
            presenter.closeBleConnection();
            DispenserWrapper dispenserWrapper = mData.get(holder.getAdapterPosition());
            context.startActivity(new Intent(context.getApplicationContext(), DispenserActivity.class)
                    .putExtra(MainActivity.INTENT_KEY_MAC_ADDRESS, dispenserWrapper.getHot().getMacAddress())
                    .putExtra(DispenserActivity.INTENT_KEY_FAVOR, dispenserWrapper.isFavor())
                    .putExtra(DispenserActivity.INTENT_KEY_ID, dispenserWrapper.getResidenceId())
                    .putExtra(DispenserActivity.INTENT_KEY_TEMPERATURE, DispenserWater.HOT.getType())
                    .putExtra(MainActivity.INTENT_KEY_LOCATION, dispenserWrapper.getLocation())
                    .putExtra(MainActivity.INTENT_KEY_DEVICE_TYPE, Device.DISPENSER.getType())
                    .putExtra(WaterDeviceBaseActivity.INTENT_PREPAY_INFO, orderPreInfo));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChooseDispenserAdaptor.DispenserWrapper dispenserWrapper = mData.get(holder.getAdapterPosition());
        holder.tv_location.setText(dispenserWrapper.getLocation());
        holder.rl_top.setOnClickListener(v -> {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemClick(v, position);
//                }
            if (lastExpandPosition != -1) {
                mData.get(lastExpandPosition).setExpanded(false);
            }
            boolean expand = false;
            if (lastExpandPosition == holder.getAdapterPosition()) {
                expand = false;
                mData.get(holder.getAdapterPosition()).setExpanded(false);
                lastExpandPosition = -1;
            } else {
                expand = true;
                mData.get(holder.getAdapterPosition()).setExpanded(true);
                lastExpandPosition = holder.getAdapterPosition();
            }
            ObjectAnimator anim =
                    ObjectAnimator.ofFloat(holder.iv_arrow,
                            "rotation", expand ? 0f : -180f, expand ? 180f : 0f);
            anim.setDuration(200);
            anim.start();
            notifyDataSetChanged();
        });
        if (dispenserWrapper.isExpanded()) {
            holder.v_divide.setVisibility(View.VISIBLE);
            holder.rl_bottom.setVisibility(View.VISIBLE);
            lastExpandPosition = holder.getAdapterPosition();
        } else {
            if (holder.rl_bottom.getVisibility() == View.VISIBLE) {
                ObjectAnimator anim =
                        ObjectAnimator.ofFloat(holder.iv_arrow,
                                "rotation", -180f, 0f);
                anim.setDuration(200);
                anim.start();
            }
            holder.v_divide.setVisibility(View.GONE);
            holder.rl_bottom.setVisibility(View.GONE);
        }
        // 添加饮水机点击事件
        // 冷水
        holder.tv_cold_water.setEnabled(dispenserWrapper.getCold() != null);
        // 冰水
        holder.tv_ice_water.setEnabled(dispenserWrapper.getIce() != null);
        // 热水
        holder.tv_hot_water.setEnabled(dispenserWrapper.getHot() != null);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_location;
        RelativeLayout rl_top;
        View v_divide;
        RelativeLayout rl_bottom;
        TextView tv_cold_water;
        TextView tv_ice_water;
        TextView tv_hot_water;
        ImageView iv_arrow;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_arrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            rl_top = (RelativeLayout) itemView.findViewById(R.id.rl_top);
            v_divide = itemView.findViewById(R.id.v_divide);
            rl_bottom = (RelativeLayout) itemView.findViewById(R.id.rl_bottom);
            tv_cold_water = (TextView) itemView.findViewById(R.id.tv_cold_water);
            tv_ice_water = (TextView) itemView.findViewById(R.id.tv_ice_water);
            tv_hot_water = (TextView) itemView.findViewById(R.id.tv_hot_water);
        }
    }

    @Data
    public static class DispenserWrapper {
        // 设备位置
        String location;
        ScanDeviceGroup deviceGroup;
        boolean expanded = false;
        boolean favor = false;
        ScanDevice cold;
        ScanDevice ice;
        ScanDevice hot;
        Long residenceId;

        public DispenserWrapper(ScanDeviceGroup device) {
            this.favor = device.getFavor();
            this.location = device.getLocation();
            this.deviceGroup = device;
            this.residenceId = device.getResidenceId();
            for (String key : device.getWater().keySet()) {
                if (TextUtils.equals(key, DispenserWater.HOT.getType())) {
                    this.hot = device.getWater().get(key);
                } else if (TextUtils.equals(key, DispenserWater.COLD.getType())) {
                    this.cold = device.getWater().get(key);
                } else if (TextUtils.equals(key, DispenserWater.ICE.getType())) {
                    this.ice = device.getWater().get(key);
                }
            }
        }
    }

}
