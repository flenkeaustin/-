package com.xiaolian.amigo.ui.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.enumeration.EvaluateStatus;
import com.xiaolian.amigo.data.enumeration.RepairStatus;
import com.xiaolian.amigo.data.network.model.dto.response.RepairDetailRespDTO;
import com.xiaolian.amigo.ui.base.WebActivity;
import com.xiaolian.amigo.ui.widget.RecycleViewDivider;
import com.xiaolian.amigo.ui.repair.adaptor.RepairProgressAdaptor;
import com.xiaolian.amigo.ui.repair.intf.IRepairDetailPresenter;
import com.xiaolian.amigo.ui.repair.intf.IRepairDetailView;
import com.xiaolian.amigo.ui.widget.photoview.AlbumItemActivity;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报修详情
 * <p>
 * Created by caidong on 2017/9/18.
 */
// TODO: 提醒客服 ／repair/remind
public class RepairDetailActivity extends RepairBaseActivity implements IRepairDetailView {

    @Inject
    IRepairDetailPresenter<IRepairDetailView> presenter;
    @BindView(R.id.rv_repair_progresses)
    RecyclerView rv_repair_progresses;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.tv_location)
    TextView tv_location;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.ll_images)
    LinearLayout ll_images;
    @BindView(R.id.iv_first)
    ImageView iv_first;
    @BindView(R.id.iv_second)
    ImageView iv_second;
    @BindView(R.id.iv_third)
    ImageView iv_third;
    @BindView(R.id.left_oper)
    TextView left_oper;
    @BindView(R.id.right_oper)
    TextView right_oper;
    @BindView(R.id.ll_extra)
    LinearLayout ll_extra;
    @BindView(R.id.tv_extra_title)
    TextView tv_extra_title;
    @BindView(R.id.tv_extra_content1)
    TextView tv_extra_content1;
    @BindView(R.id.tv_extra_content2)
    TextView tv_extra_content2;

    List<RepairProgressAdaptor.ProgressWrapper> progresses = new ArrayList<>();
    Long detailId;

    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;

    private ArrayList<String> images = new ArrayList<>();

    @Override
    protected void initView() {
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);

        presenter.onAttach(this);

        adapter = new RepairProgressAdaptor(progresses);
        rv_repair_progresses.addItemDecoration(new RecycleViewDivider(this, RecycleViewDivider.VERTICAL_LIST, "deductLast"));
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_repair_progresses.setLayoutManager(manager);
        rv_repair_progresses.setAdapter(adapter);

        presenter.requestRepailDetail(detailId);
    }

    @Override
    protected int setTitle() {
        return R.string.repair_detail;
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_repair_detail;
    }

    @Override
    public void addMoreProgresses(List<RepairProgressAdaptor.ProgressWrapper> progresses) {
        Collections.reverse(progresses);
        this.progresses.addAll(progresses);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void render(RepairDetailRespDTO detail) {
        tv_type.setText(Device.getDevice(detail.getDeviceType()).getDesc());
        tv_location.setText(detail.getLocation());
        tv_content.setText(detail.getContent());

        List<String> images = detail.getImages();
        if(null != images){
            this.images.addAll(images);
            // 获取图片数量
            int num = images.size();
            RequestManager manager = Glide.with(this);
            // 渲染第一张图
            if (num > 0) {
                ll_images.setVisibility(View.VISIBLE);
                iv_first.setVisibility(View.VISIBLE);
                manager.load(images.get(0))
                        .asBitmap()
                        .placeholder(R.drawable.ic_picture_error)
                        .error(R.drawable.ic_picture_error)
                        .into(iv_first);
            }
            // 渲染第二张图
            if (num > 1) {
                iv_second.setVisibility(View.VISIBLE);
                manager.load(images.get(1))
                        .asBitmap()
                        .placeholder(R.drawable.ic_picture_error)
                        .error(R.drawable.ic_picture_error)
                        .into(iv_second);
            }
            // 渲染第三张图
            if (num > 2) {
                iv_third.setVisibility(View.VISIBLE);
                manager.load(images.get(2))
                        .asBitmap()
                        .placeholder(R.drawable.ic_picture_error)
                        .error(R.drawable.ic_picture_error)
                        .into(iv_third);
            }
        }

        // 设置操作按钮中的显示文案
        RepairStatus status = RepairStatus.getStatus(detail.getSteps().get(detail.getSteps().size()-1).getStatus());
        String[] opers = status.getNextOperations();
        left_oper.setText(opers[0]);
        right_oper.setText(opers[1]);
        left_oper.setOnClickListener(v -> {
            switch (status) {
                case REPAIR_DONE:
                    Intent intent = new Intent(RepairDetailActivity.this, RepairEvaluationActivity.class);
                    intent.putExtra(RepairEvaluationActivity.INTENT_KEY_REPAIR_EVALUATION_ID, detail.getId());
                    intent.putExtra(RepairEvaluationActivity.INTENT_KEY_REPAIR_EVALUATION_REPAIR_MAN_NAME, detail.getRepairmanName());
                    intent.putExtra(RepairEvaluationActivity.INTENT_KEY_REPAIR_EVALUATION_DEVICE_LOCATION, detail.getLocation());
                    intent.putExtra(RepairEvaluationActivity.INTENT_KEY_REPAIR_EVALUATION_DEVICE_TYPE, detail.getDeviceType());
                    intent.putExtra(RepairEvaluationActivity.INTENT_KEY_REPAIR_EVALUATION_TIME, CommonUtil.stampToDate(detail.getSteps().get(0).getTime()));
                    startActivity(intent);
                    break;
                case AUDIT_FAIL:
                    startActivity(new Intent(RepairDetailActivity.this, WebActivity.class)
                            .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_HELP));
                    break;
                case AUDIT_PENDING:
                case REPAIR_PENDING:
                case REPAIRING:
                    presenter.remind(detail.getId());
                    break;
                default:
                    break;
            }
        });
        right_oper.setOnClickListener(v -> {
            switch (status) {
                case REPAIR_DONE:
                    // TODO 我要投诉
                    break;
                case REPAIR_PENDING:
                case REPAIRING:
                case AUDIT_PENDING:
                    startActivity(new Intent(RepairDetailActivity.this, WebActivity.class)
                            .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_HELP));
                    break;
                case AUDIT_FAIL:
                    CommonUtil.call(RepairDetailActivity.this, detail.getCsMobile());
                    break;
            }
        });
        if (EvaluateStatus.getStatus(detail.getRated()) == EvaluateStatus.EVALUATE_DONE) {
            left_oper.setEnabled(false);
            left_oper.setTextColor(ContextCompat.getColor(this, R.color.colorDark6));
            ll_extra.setVisibility(View.VISIBLE);
            tv_extra_title.setText("评价信息");
            tv_extra_content1.setVisibility(View.VISIBLE);
            tv_extra_content1.setText(detail.getScore() + "分");
            tv_extra_content2.setText(detail.getComment());
        }
        if (status == RepairStatus.AUDIT_FAIL) {
            ll_extra.setVisibility(View.VISIBLE);
            tv_extra_title.setText("客服回复");
            tv_extra_content2.setText(detail.getReply());
        }
    }

    @Override
    protected void setUp() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Constant.EXTRA_KEY);
        detailId = bundle.getLong(Constant.BUNDLE_ID);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        presenter.clearObservers();
        progresses.clear();
        super.onDestroy();
    }

    @OnClick(R.id.iv_first)
    void onFirstImageClick() {
        if (images != null) {
            Intent intent = new Intent(this, AlbumItemActivity.class);
            intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, 0);
            intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_LIST, images);
            startActivity(intent);
        }
    }

    @OnClick(R.id.iv_second)
    void onSecondImageClick() {
        if (images != null) {
            Intent intent = new Intent(this, AlbumItemActivity.class);
            intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, 1);
            intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_LIST, images);
            startActivity(intent);
        }
    }

    @OnClick(R.id.iv_third)
    void onThirdImageClick() {
        if (images != null) {
            Intent intent = new Intent(this, AlbumItemActivity.class);
            intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, 2);
            intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_LIST, images);
            startActivity(intent);
        }
    }

}
