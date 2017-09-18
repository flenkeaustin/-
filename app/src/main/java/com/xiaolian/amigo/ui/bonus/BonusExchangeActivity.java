package com.xiaolian.amigo.ui.bonus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.bonus.intf.IBonusExchangePresenter;
import com.xiaolian.amigo.ui.bonus.intf.IBonusExchangeView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 兑换红包
 * @author zcd
 */

public class BonusExchangeActivity extends BonusBaseActivity implements IBonusExchangeView {


    @BindView(R.id.et_change_code)
    EditText et_changeCode;

    @BindView(R.id.bt_submit)
    Button button;

    @Inject
    IBonusExchangePresenter<IBonusExchangeView> presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_exchange);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);

        presenter.onAttach(BonusExchangeActivity.this);
    }

    @Override
    protected void setUp() {

    }

}
