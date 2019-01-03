package com.xiaolian.amigo.ui.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.ScreenUtils;
import com.xiaolian.amigo.util.ShareUtils;

/**
 * 分享
 */
public class ShareAlertDialog implements View.OnClickListener {

    private Context context ;
    private Dialog dialog ;

    private String shareUrl  = "分享笑联";
    private ImageView copy ;
    private TextView txtCopy ;
    private ImageView qq ;
    private TextView txtQQ ;
    private ImageView wx ;
    private TextView txtWX ;
    private TextView cancel ;

    private Toast toast;
    private CountDownTimer toastCountDown;

    public ShareAlertDialog(Context context , String shareUrl){
        this.context = context ;
        this.shareUrl = shareUrl ;
    }

    public ShareAlertDialog Builder(){

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        copy = view.findViewById(R.id.copy);
        txtCopy = view.findViewById(R.id.copy_txt);
        qq = view.findViewById(R.id.qq);
        txtQQ = view.findViewById(R.id.qq_txt);
        wx = view.findViewById(R.id.wx);
        txtWX = view.findViewById(R.id.wx_txt);
        cancel = view.findViewById(R.id.cancel);
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        initListener();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT ;
        lp.x = 0;
        lp.y = 0;
        return this ;
    }

    private void initListener(){
        copy.setOnClickListener(this);
        txtCopy.setOnClickListener(this);
        qq.setOnClickListener(this);
        txtQQ.setOnClickListener(this);
        wx.setOnClickListener(this);
        txtWX.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void copy(){
        CommonUtil.copy(shareUrl, context);
        showSuccessToast("复制成功");
        if (dialog != null) dialog.dismiss();
    }


    private void shareWX(){
        ShareUtils.shareWX(shareUrl ,"" ,context);
        dialog.dismiss();
    }

    public void show(){
        if (dialog != null){
            dialog.show();
        }
    }

    public void finish(){
        this.context = null ;
        if (dialog != null){
            if (dialog.isShowing()) dialog.dismiss();
            dialog.cancel();
        }

        dialog = null ;
    }



    private void showSuccessToast(String message) {
        if (toast == null) {
            toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        if (toastCountDown == null) {
            toastCountDown = new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    toast.cancel();
                }
            };
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.view_toast, null, false);
        TextView tv_content = layout.findViewById(R.id.tv_content);
        tv_content.setText(message);
        toast.setView(layout);
        toast.show();
        toastCountDown.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.copy:
                copy();
                break;
            case R.id.copy_txt:
                copy();
                break;
            case R.id.cancel:
                if (dialog != null){
                    dialog.dismiss();
                }
                break;
            case R.id.qq:
                break;
            case R.id.qq_txt:
                break;
            case R.id.wx:
                shareWX();
                break;
            case R.id.wx_txt:
                shareWX();
                break;
                default:
                    break;
        }
    }
}
