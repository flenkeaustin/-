package com.xiaolian.amigo.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaolian.amigo.MvpApp;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.di.componet.DaggerUserActivityComponent;
import com.xiaolian.amigo.di.componet.UserActivityComponent;
import com.xiaolian.amigo.di.module.UserActivityModule;
import com.xiaolian.amigo.ui.base.BaseActivity;
import com.xiaolian.amigo.ui.repair.adaptor.ImageAddAdapter;
import com.xiaolian.amigo.ui.user.intf.IUserCerticifationStatusPresenter;
import com.xiaolian.amigo.ui.user.intf.IUserCertificationStatusView;
import com.xiaolian.amigo.ui.widget.GridSpacesItemDecoration;
import com.xiaolian.amigo.ui.widget.photoview.AlbumItemActivity;
import com.xiaolian.amigo.util.ScreenUtils;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.xiaolian.amigo.util.Constant.USER_INFO_ACTIVITY_SRC;

public class UserCertificationStatusActivity extends BaseActivity implements IUserCertificationStatusView {


    private UserActivityComponent mActivityComponent;

    @Inject
    IUserCerticifationStatusPresenter<IUserCertificationStatusView> presenter ;

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_text)
    TextView tvToolbarText;
    @BindView(R.id.tv_toolbar_iv)
    ImageView tvToolbarIv;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.tv_profession)
    TextView tvProfession;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.tv_studentId)
    TextView tvStudentId;
    @BindView(R.id.tv_dormitory)
    TextView tvDormitory;
    @BindView(R.id.student_card_id)
    RecyclerView studentCardId;
    @BindView(R.id.card_id)
    RecyclerView cardId;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.sv_main_container)
    ScrollView svMainContainer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.view_line)
    View viewLine;

    private Unbinder unbinder;

    private int screenWidth;
    private int imageWidth;

    private ImageAddAdapter studentIdAdapter;
    List<ImageAddAdapter.ImageItem> studentIdImages = new ArrayList<>();


    private ImageAddAdapter cardIdAdapter ;  // 身份证
    List<ImageAddAdapter.ImageItem> cardIdImages = new ArrayList<>();

    @Override
    protected void setUp() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_certification_status);
        unbinder = ButterKnife.bind(this);
        initJect();
        initScrollView();
        initView();
        initImageAdd();

    }

    private void initView(){
        presenter.onAttach(this);
    }



    /**
     * 滑动响应
     */
    private void initScrollView() {
        IOverScrollDecor iOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(svMainContainer);
        iOverScrollDecor.setOverScrollUpdateListener((decor, state, offset) -> {
            if (offset < -(tvToolbarTitle.getHeight()) + tvToolbarTitle.getPaddingTop()) {
                setTitleVisiable(View.VISIBLE);
            } else {
                setTitleVisiable(View.GONE);
            }
        });

    }

    private void initJect(){
        mActivityComponent = DaggerUserActivityComponent.builder()
                .userActivityModule(new UserActivityModule(this))
                .applicationComponent(((MvpApp) getApplication()).getComponent())
                .build();
        mActivityComponent.inject(this);
    }

    private void setTitleVisiable(int visiable) {
        tvTitle.setVisibility(visiable);
        viewLine.setVisibility(visiable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.onDetach();
    }


    @OnClick(R.id.certification)
    public void certification(){
        startActivity(this ,UserCertificationActivity.class);
    }

    @OnClick(R.id.change_dormitory)
    public void changeDormitory(){
        Intent intent ;
        intent = new Intent(this, ListChooseActivity.class);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_IS_EDIT, false);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_CHOOSE_ACTION,
                ListChooseActivity.ACTION_LIST_BUILDING);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_SRC_ACTIVITY, USER_INFO_ACTIVITY_SRC);
        intent.putExtra(ListChooseActivity.INTENT_KEY_LIST_DEVICE_TYPE, Device.HEATER.getType());
        startActivity(intent);
    }


    private void initImageAdd() {
        cardIdAdapter = new ImageAddAdapter(this, R.layout.item_image_add, cardIdImages);
        cardIdAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(UserCertificationStatusActivity.this, AlbumItemActivity.class);
                    intent.putExtra(AlbumItemActivity.INTENT_POSITION, position);
                    intent.putExtra(AlbumItemActivity.EXTRA_TYPE_SINGLE, cardIdImages.get(position).getImageUrl());
                    intent.putExtra(AlbumItemActivity.INTENT_ACTION, AlbumItemActivity.ACTION_NORMAL);
                    startActivity(intent);
                }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        cardId.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardId.addItemDecoration(new GridSpacesItemDecoration(3, ScreenUtils.dpToPxInt(this, 10), false));
        cardId.setAdapter(cardIdAdapter);


        studentIdAdapter = new ImageAddAdapter(this, R.layout.item_image_add, studentIdImages);
        studentIdAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(UserCertificationStatusActivity.this, AlbumItemActivity.class);
                intent.putExtra(AlbumItemActivity.INTENT_POSITION, position);
                intent.putExtra(AlbumItemActivity.EXTRA_TYPE_SINGLE, studentIdImages.get(position).getImageUrl());
                intent.putExtra(AlbumItemActivity.INTENT_ACTION, AlbumItemActivity.ACTION_NORMAL);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        studentCardId.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        studentCardId.addItemDecoration(new GridSpacesItemDecoration(3, ScreenUtils.dpToPxInt(this, 10), false));
        studentCardId.setAdapter(studentIdAdapter);
    }




}