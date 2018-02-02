package com.xiaolian.amigo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;

/**
 * 未找零金额dialog
 *
 * @author zcd
 * @date 17/10/12
 */

public class PrepayDialog extends Dialog {
    private TextView tvTip;
    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvOk;
    private OnOkClickListener okClickListener;
    private OnCancelClickListener cancelClickListener;

    public PrepayDialog(@NonNull Context context) {
        super(context, R.style.AlertDialogStyle);
        Window window = this.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_prepay);
        tvTitle = findViewById(R.id.tv_title);
        tvTip = findViewById(R.id.tv_tip);
        tvCancel = findViewById(R.id.tv_cancel);
        tvOk = findViewById(R.id.tv_ok);
        tvCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onCancelClick(this);
                dismiss();
            }
        });
        tvOk.setOnClickListener(v -> {
            if (okClickListener != null) {
                okClickListener.onOkClick(this);
                dismiss();
            }
        });
    }

    public void setDeviceTypeAndPrepaySize(int type, int size) {
        String tip1 = "你在\"";
        String tip2 = "\"中有预付金额还未找零";
        String deviceStr = Device.getDevice(type).getDesc();
        SpannableString builder = new SpannableString(tip1 + deviceStr + tip2);
        tvTip.setText(builder);
        tvTitle.setText("你有" + size + "笔未找零金额！");
    }

    public void setOnOkClickListener(OnOkClickListener listener) {
        this.okClickListener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
    }

    public interface OnOkClickListener {
        void onOkClick(Dialog dialog);
    }

    public interface OnCancelClickListener {
        void onCancelClick(Dialog dialog);
    }

}
