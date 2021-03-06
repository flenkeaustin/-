package com.xiaolian.amigo.ui.notice.adaptor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Notice;
import com.xiaolian.amigo.data.enumeration.NoticeReadStatus;
import com.xiaolian.amigo.data.vo.Notify;
import com.xiaolian.amigo.util.TimeUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 通知列表Adaptor
 *
 * @author zcd
 * @date 17/9/22
 */

public class NoticeAdaptor extends CommonAdapter<NoticeAdaptor.NoticeWapper> {
    private static final String DIVIDER_TAG = "divider";
    private Drawable dot;

    public NoticeAdaptor(Context context, int layoutId, List<NoticeWapper> datas) {
        super(context, layoutId, datas);
        dot = ContextCompat.getDrawable(context, R.drawable.dot_notice_red);
        dot.setBounds(0, 0, dot.getIntrinsicWidth(), dot.getIntrinsicHeight());
    }

    @Override
    protected void convert(ViewHolder holder, NoticeWapper noticeWapper, int position) {
//        if (position == 0) {
//            LinearLayout llItem = holder.getView(R.id.ll_item_notice);
//            if (!TextUtils.equals((CharSequence) llItem.getChildAt(0).getTag(), DIVIDER_TAG)) {
//                View view = new View(llItem.getContext());
//                view.setTag(DIVIDER_TAG);
//                view.setBackgroundResource(R.drawable.divider);
//                llItem.addView(view, 0);
//            }
//        }

        TextView tvType = holder.getView(R.id.tv_type);
        if (ObjectsCompat.equals(noticeWapper.getReadStatus(), NoticeReadStatus.UNREAD.getType())
                && noticeWapper.getType() != Notice.EMERGENCY.getType()) {
            // 未读 且不为紧急通知
            tvType.setCompoundDrawables(tvType.getCompoundDrawables()[0],
                    tvType.getCompoundDrawables()[1], dot, tvType.getCompoundDrawables()[3]);
        } else if (ObjectsCompat.equals(noticeWapper.getReadStatus(), NoticeReadStatus.READ.getType())
                || noticeWapper.getType() == Notice.EMERGENCY.getType()) {
            tvType.setCompoundDrawables(tvType.getCompoundDrawables()[0],
                    tvType.getCompoundDrawables()[1], null, tvType.getCompoundDrawables()[3]);
        }
        if (noticeWapper.getType() != null) {
            Notice notice = Notice.getNotice(noticeWapper.getType());
            if (notice != null) {
                holder.setImageResource(R.id.iv_image, notice.getDrawableRes());
                holder.setText(R.id.tv_type, notice.getDesc());
            }
        }
        holder.setText(R.id.tv_content, noticeWapper.getContent());
//        holder.setText(R.id.tv_time, TimeUtils.convertTimestampToFormat(noticeWapper.getCreateTime())
//                + "/" + TimeUtils.millis2String(noticeWapper.getCreateTime(), TimeUtils.MY_TIME_FORMAT));
        holder.setText(R.id.tv_time, TimeUtils.noticeTimestampFormat(noticeWapper.getCreateTime()));
    }

    @Data
    public static class NoticeWapper implements Serializable {
        private String content;
        private Long id;
        private Integer type;
        private Long createTime;
        private Integer readStatus;

        public NoticeWapper(Notify notify) {
            this.content = notify.getContent();
            this.id = notify.getId();
            this.type = notify.getType();
            this.createTime = notify.getCreateTime();
            this.readStatus = notify.getReadStatus();
        }
    }
}
