package com.xiaolian.amigo.ui.bonus.adaptor;

import android.content.Context;

import com.xiaolian.amigo.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 过期红包
 * <p>
 * Created by zcd on 9/18/17.
 */

public class ExpiredBonusAdaptor extends CommonAdapter<BonusAdaptor.BonusWrapper> {
    public ExpiredBonusAdaptor(Context context, int layoutId, List<BonusAdaptor.BonusWrapper> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, BonusAdaptor.BonusWrapper bonusWrapper, int position) {
        holder.setText(R.id.tv_amount, bonusWrapper.amount.toString());
        holder.setText(R.id.tv_type, bonusWrapper.type.toString());
        holder.setText(R.id.tv_time_end, bonusWrapper.timeEnd);
        holder.setText(R.id.tv_desc, bonusWrapper.desc);
        holder.setText(R.id.tv_time_left, bonusWrapper.timeLeft.toString());
    }
}