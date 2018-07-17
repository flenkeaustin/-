package com.xiaolian.amigo.ui.user;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.widget.ClearableEditText;
import com.xiaolian.amigo.util.Constant;
import com.xiaolian.amigo.util.CountDownButtonHelper;
import com.xiaolian.amigo.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


/**
 * 浴室密码找回第一步
 */
public class FindBathroomPasswordStep1Fragment extends Fragment {


    private static final int MOBILE_LENGTH = 1;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.bt_send_verification_code)
    Button btSendVerificationCode;
    @BindView(R.id.et_verification_code)
    ClearableEditText etVerificationCode;
    @BindView(R.id.bt_submit)
    Button btSubmit;

    Unbinder unbind;
    CountDownButtonHelper cdb ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_bathroom_password_verification_code, container, false);
        unbind =ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtil.setEditHintAndSize(getString(R.string.please_enter_verify_code), 14, etVerificationCode);
        ViewUtil.setEditHintAndSize(getString(R.string.mobile_hint) , 14 , etMobile);
        cdb = new CountDownButtonHelper(btSendVerificationCode ,getString(R.string.get_verification_code) ,
                Constant.VERIFY_CODE_TIME , 1 );

        cdb.setOnFinishListener(new CountDownButtonHelper.OnFinishListener() {
            @Override
            public void finish() {
                if (getContext() != null){
                    setBtSendVerificationCodeEnable(btSendVerificationCode , true);
                }
            }
        });

        setBtSendVerificationCodeEnable(btSendVerificationCode , false);

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toggleButton();
                if (etMobile.length() >= MOBILE_LENGTH ){
                    setBtSendVerificationCodeEnable(btSendVerificationCode , true);
                }else{
                    setBtSendVerificationCodeEnable(btSendVerificationCode , false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        toggleButton();
    }



    public void startTimer() {
        int color = ContextCompat.getColor(getContext(), R.color.colorDarkB);
        btSendVerificationCode.setTextColor(color);
        btSendVerificationCode.setBackgroundResource(R.drawable.bg_rect_gray_stroke);
        cdb.start();
    }


    @OnTextChanged(R.id.et_verification_code)
    public void onVerificationCode(){toggleButton();}

    private void toggleButton() {
        boolean valid = !TextUtils.isEmpty(etVerificationCode.getText())
                && !TextUtils.isEmpty(etMobile.getText())
                && etMobile.getText().length() >= MOBILE_LENGTH;
        btSubmit.setEnabled(valid);
    }

    /**
     * 设置获取验证码按钮是否可点击按钮变化
     * @param button
     */
    private void setBtSendVerificationCodeEnable(Button button , boolean isEnable){
        button.setEnabled(isEnable);
        if (isEnable) {
            button.setText(getString(R.string.get_verification_code));
            int color = ContextCompat.getColor(getContext(), R.color.colorFullRed);
            button.setTextColor(color);
            button.setBackgroundResource(R.drawable.bg_rect_red_stroke);
        }else{
            int color = ContextCompat.getColor(getContext(), R.color.colorDarkB);
            btSendVerificationCode.setTextColor(color);
            btSendVerificationCode.setBackgroundResource(R.drawable.bg_rect_gray_stroke);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbind != null) unbind.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (cdb != null) cdb.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (etMobile != null){
            etMobile.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null){
                imm.showSoftInput(etMobile ,InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
}
