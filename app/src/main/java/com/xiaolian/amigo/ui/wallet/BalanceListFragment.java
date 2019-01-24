package com.xiaolian.amigo.ui.wallet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.xiaolian.amigo.data.enumeration.WithdrawOperationType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.wallet.adaptor.BillListAdaptor;
import com.xiaolian.amigo.ui.wallet.adaptor.WithdrawalAdaptor;
import com.xiaolian.amigo.ui.widget.indicator.RefreshLayoutFooter;
import com.xiaolian.amigo.ui.widget.indicator.RefreshLayoutHeader;
import com.xiaolian.amigo.util.Constant;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;

public class BalanceListFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout llFooter;
    private LinearLayout llHeader;
    private RelativeLayout rlEmpty;
    private TextView tvEmptyTip;
    private RelativeLayout rlError;
//    private View v_divide;

//    protected int page = Constant.PAGE_START_NUM;
    private SmartRefreshLayout refreshLayout;
//    private boolean autoRefresh = true;
//    private boolean refreshFlag = false;

    private List<BillListAdaptor.BillListAdaptorWrapper> items = new ArrayList<>();
    private BillListAdaptor adaptor;


    private String timeStr;

    private Long lastId;

    private Integer billType;

    private Integer billStatus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_balance_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llHeader = view.findViewById(R.id.ll_header);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        rlEmpty = view.findViewById(R.id.rl_empty);
        tvEmptyTip = view.findViewById(R.id.tv_empty_tip);
        rlError = view.findViewById(R.id.rl_error);
        llFooter = view.findViewById(R.id.ll_footer);
        initRecyclerView();
        initTimeStr();
    }

    private void initTimeStr() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH )+1;
        timeStr = String.valueOf(currentYear * 100 + currentMonth);
    }

    private void initRecyclerView() {
        setRecyclerView(recyclerView);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                BalanceListFragment.this.onLoadMore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                BalanceListFragment.this.onRefresh();
            }
        });
        refreshLayout.setRefreshHeader(new RefreshLayoutHeader(getContext()));
        refreshLayout.setRefreshFooter(new RefreshLayoutFooter(getContext()));
        refreshLayout.setReboundDuration(200);
//        if (autoRefresh) {
            refreshLayout.autoRefresh(20);
//        }
    }

    protected void setRecyclerView(RecyclerView recyclerView) {
        adaptor = new BillListAdaptor(getActivity(), R.layout.item_withdrawal_record, items);
        adaptor.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                BillListAdaptor.BillListAdaptorWrapper item = items.get(position);
                //点击后跳转到账单详情
                if (item.getType() == BillListAdaptor.XLFilterContentViewBillTypeRecharge || item.getType() == BillListAdaptor.XLFilterContentViewBillTypeWithdraw) /*余额充值、退款跳转*/{
                    ((BalanceDetailListActivity)getActivity()).gotoBillRechargeWithdrawActivity(item.getType(), item.getId());
                } else /*跳转到消费账单页面（包含预付待找零）*/{
                    ((BalanceDetailListActivity)getActivity()).gotoBillDetailActivity(item.getType(), item.getId(), item.getStatus());
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptor);
    }

    void onRefresh() {
//        refreshFlag = true;
        if (items.size() > 0) {
            BillListAdaptor.BillListAdaptorWrapper item = items.get(0);
            lastId = item.getDetailId();
        }

        ((BalanceDetailListActivity)getActivity()).presenter.getUserBillList("201901", billType, billStatus, lastId, true, 20);

    }

    void onLoadMore() {
        if (items.size() > 0) {
            BillListAdaptor.BillListAdaptorWrapper item = items.get(items.size()-1);
            lastId = item.getDetailId();
        }
        ((BalanceDetailListActivity)getActivity()).presenter.getUserBillList("201901", billType, billStatus, lastId, false, 20);
    }

//    private void initFooter() {
//        if (setFooterLayout() > 0) {
//            View layout = LayoutInflater.from(getActivity()).inflate(setFooterLayout(), null, true);
//            llFooter.addView(refreshLayout);
//            llFooter.setVisibility(View.VISIBLE);
//        }
//    }
//
//    protected void setHeaderBackground(@ColorRes int color) {
//        llHeader.setBackgroundResource(color);
//    }
//
//    protected @LayoutRes
//    int setFooterLayout() {
//        return 0;
//    }

    public void setLoadMoreComplete() {
        refreshLayout.finishLoadMore();
    }

    public void setRefreshComplete() {
        refreshLayout.finishRefresh(300);
    }

    public void addMore(List<BillListAdaptor.BillListAdaptorWrapper> wrappers) {

        if (wrappers.size() <= 0)  /*没有新的数据*/ {
            return;
        }

        if (items.size() <= 0)  /*第一次请求数据*/{
            items.addAll(wrappers);
            adaptor.notifyDataSetChanged();
            return;
        }
        //取新加载的数据的第一条和已有的数据的最后一条做比较，新加载的时间戳大则表示拉取的最新的，否则拉取的是历史数据
        BillListAdaptor.BillListAdaptorWrapper newItem = wrappers.get(0);
        BillListAdaptor.BillListAdaptorWrapper oldItem = items.get(wrappers.size()-1);
        if (newItem.getCreateTime() > oldItem.getCreateTime()) /*加载的是新数据*/{
            items.addAll(0,wrappers);
        } else {
            items.addAll(wrappers);
        }
        adaptor.notifyDataSetChanged();
    }

    public void showEmptyView() {
        showEmptyView(getString(R.string.empty_tip), R.color.colorBackgroundGray);
    }

    public void showEmptyView(int tipRes, int colorRes) {
        showEmptyView(getString(tipRes), colorRes);
    }

    public void showEmptyView(int tipRes) {
        showEmptyView(tipRes, R.color.colorBackgroundGray);
    }

    public void showEmptyView(String tip, int colorRes) {
        if (items.size() <= 0) {
            rlEmpty.setVisibility(View.VISIBLE);
            rlEmpty.setBackgroundResource(colorRes);
            tvEmptyTip.setText(tip);
        }
    }

    public void hideEmptyView() {
        rlEmpty.setVisibility(View.GONE);
    }

    public void showErrorView() {
        showErrorView(R.color.colorBackgroundGray);
    }

    public void showErrorView(int colorRes) {
        rlError.setVisibility(View.VISIBLE);
        rlError.setBackgroundResource(colorRes);
    }

    public void hideErrorView() {
        rlError.setVisibility(View.GONE);
    }

}
