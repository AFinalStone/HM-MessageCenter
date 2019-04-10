package com.hm.iou.msg.business.contract.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.contract.ContractMsgContract;
import com.hm.iou.msg.business.contract.ContractMsgPresenter;
import com.hm.iou.msg.business.hmmsg.view.HmMsgListAdapter;
import com.hm.iou.msg.business.message.view.ChatMsgModel;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class ContractMsgActivity extends BaseActivity<ContractMsgPresenter> implements ContractMsgContract.View {


    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;

    HmMsgListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_contract_msg;
    }

    @Override
    protected ContractMsgPresenter initPresenter() {
        return new ContractMsgPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {

        mAdapter = new HmMsgListAdapter();
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (R.id.ll_adOrSport == view.getId()) {
                    NavigationHelper.toContractMsgDetailPage(mContext);
                }
            }
        });
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getContractList();
            }
        });

        mPresenter.getContractList();
    }

    @Override
    public void showMsgList(List<ChatMsgModel> list) {

    }

    @Override
    public void enableRefresh() {

    }

    @Override
    public void hidePullDownRefresh() {

    }

    @Override
    public void showInitLoading() {

    }

    @Override
    public void hideInitLoading() {

    }
}
