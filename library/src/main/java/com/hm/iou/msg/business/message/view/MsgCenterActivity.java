package com.hm.iou.msg.business.message.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.message.MsgCenterContract;
import com.hm.iou.msg.business.message.MsgCenterPresenter;
import com.hm.iou.uikit.HMGrayDividerItemDecoration;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class MsgCenterActivity extends BaseActivity<MsgCenterPresenter> implements MsgCenterContract.View {


    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.ll_data_empty)
    LinearLayout mllDataEmpty;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;

    MsgListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msg_activity_msg_center;
    }

    @Override
    protected MsgCenterPresenter initPresenter() {
        return new MsgCenterPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mAdapter = new MsgListAdapter();
        mAdapter.bindToRecyclerView(mRvMsgList);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mRvMsgList.addItemDecoration(new HMGrayDividerItemDecoration(mContext, LinearLayout.VERTICAL));
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IMsgItem item = (IMsgItem) adapter.getItem(position);
                NavigationHelper.ToMsgDetail(mContext, item.getMsgDetailLinkUrl());
                mPresenter.markHaveRead(position);
            }
        });
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMsgList();
            }
        });
        mPresenter.init();
    }

    @Override
    public void showInitLoading() {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataLoading();
    }

    @Override
    public void showInitLoadingFailed(String failedMsg) {
        mLoadingInit.showDataFail(failedMsg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.init();
            }
        });
    }

    @Override
    public void hideInitLoading() {
        mLoadingInit.setVisibility(View.GONE);
    }

    @Override
    public void showMsgList(List<IMsgItem> list) {
        mllDataEmpty.setVisibility(View.GONE);
        mRvMsgList.setVisibility(View.VISIBLE);
        mAdapter.setNewData(list);
    }

    @Override
    public void refreshItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void showPullDownRefresh() {
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void showDataEmpty() {
        mRvMsgList.setVisibility(View.GONE);
        mllDataEmpty.setVisibility(View.VISIBLE);
    }


}
