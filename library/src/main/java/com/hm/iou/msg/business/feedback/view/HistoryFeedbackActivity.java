package com.hm.iou.msg.business.feedback.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;

import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.feedback.HistoryFeedbackContract;
import com.hm.iou.msg.business.feedback.presenter.HistoryFeedbackPresenter;
import com.hm.iou.router.Router;
import com.hm.iou.uikit.HMGrayDividerItemDecoration;
import com.hm.iou.uikit.HMLoadMoreView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.HMTopBarView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hjy on 2018/5/28.
 */

public class HistoryFeedbackActivity extends BaseActivity<HistoryFeedbackPresenter> implements HistoryFeedbackContract.View {

    @BindView(R2.id.smartrl_feedback_list)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.recyclerView_feedback_list)
    RecyclerView mRecyclerView;
    @BindView(R2.id.lv_feedback_loading)
    HMLoadingView mLoadingView;
    @BindView(R2.id.topbar)
    HMTopBarView mTopBarView;

    private FeedbackListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msg_activity_feedback_history_list;
    }

    @Override
    protected HistoryFeedbackPresenter initPresenter() {
        return new HistoryFeedbackPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mAdapter = new FeedbackListAdapter(this);
        mAdapter.setLoadMoreView(new HMLoadMoreView());
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HMGrayDividerItemDecoration(this, LinearLayout.VERTICAL));

        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refresh();
            }
        });

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        }, mRecyclerView);

        //设置点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IFeedbackListItem item = (IFeedbackListItem) adapter.getItem(position);
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/message/feedback_detail")
                        .withString("feedback_id", item.getFeedbackId())
                        .navigation(HistoryFeedbackActivity.this);
            }
        });
        //显示正在加载中
        mLoadingView.showDataLoading();
        mPresenter.refresh();
    }

    @Override
    public void showLoadMoreEnd() {
        mAdapter.loadMoreEnd();
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            mLoadingView.showDataLoading();
        } else {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDataEmpty() {
        mLoadingView.showDataEmpty("");
    }

    @Override
    public void showDataLoadFail() {
        mLoadingView.showDataFail(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingView.showDataLoading();
                mPresenter.refresh();
            }
        });
    }

    @Override
    public void showFeedbackList(List<IFeedbackListItem> list) {
        mAdapter.setNewData(list);
    }
}