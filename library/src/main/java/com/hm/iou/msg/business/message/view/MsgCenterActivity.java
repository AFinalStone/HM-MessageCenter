package com.hm.iou.msg.business.message.view;

import android.content.Intent;
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
import com.hm.iou.router.Router;
import com.hm.iou.uikit.HMGrayDividerItemDecoration;
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
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.bindToRecyclerView(mRvMsgList);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mRvMsgList.addItemDecoration(new HMGrayDividerItemDecoration(mContext, LinearLayout.VERTICAL));
        mAdapter.setEmptyView(R.layout.msg_item_msg_center_empty);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IMsgItem item = (IMsgItem) adapter.getItem(position);
                NavigationHelper.ToMsgDetail(mContext, item.getMsgDetailLinkUrl());
            }
        });
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMsgList();
            }
        });
    }

    @Override
    public void showMsgList(List<IMsgItem> list) {
        mAdapter.addData(list);
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
    }
}
