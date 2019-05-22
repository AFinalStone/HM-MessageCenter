package com.hm.iou.msg.business.similarity.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.similarity.SimilarityContractMsgContract;
import com.hm.iou.msg.business.similarity.SimilarityContractMsgPresenter;
import com.hm.iou.tools.StatusBarUtil;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadMoreView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.hm.iou.uikit.dialog.HMAlertDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class SimilarityContractMsgActivity extends BaseActivity<SimilarityContractMsgPresenter> implements SimilarityContractMsgContract.View {

    @BindView(R2.id.view_statusbar_placeholder)
    View mViewStatusBar;
    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;
    @BindView(R2.id.bottomBar)
    HMBottomBarView mBottomBar;

    SimilarityContractListAdapter mAdapter;
    HMAlertDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_similarity_contract;
    }

    @Override
    protected SimilarityContractMsgPresenter initPresenter() {
        return new SimilarityContractMsgPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
        if (statusBarHeight > 0) {
            ViewGroup.LayoutParams params = mViewStatusBar.getLayoutParams();
            params.height = statusBarHeight;
            mViewStatusBar.setLayoutParams(params);
        }
        mBottomBar.setOnTitleClickListener(new HMBottomBarView.OnTitleClickListener() {
            @Override
            public void onClickTitle() {
                if (mDialog == null) {
                    mDialog = new HMAlertDialog.Builder(mContext)
                            .setTitle("清扫未读状态")
                            .setMessage("把所有“未读”消息标成“已读”状态吗？")
                            .setNegativeButton("取消")
                            .setPositiveButton("全部已读")
                            .setOnClickListener(new HMAlertDialog.OnClickListener() {
                                @Override
                                public void onPosClick() {
                                    mPresenter.makeTypeMsgHaveRead("aliPay");
                                }

                                @Override
                                public void onNegClick() {

                                }
                            })
                            .create();
                }
                mDialog.show();
            }
        });
        mAdapter = new SimilarityContractListAdapter(mContext);
        mAdapter.setLoadMoreView(new HMLoadMoreView());
        mAdapter.setHeaderAndEmpty(true);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ISimilarityContractMsgItem item = (ISimilarityContractMsgItem) adapter.getItem(position);
                if (item != null) {
                    NavigationHelper.toMsgDetailPage(mContext, item.getIJustUrl());
                }
            }
        });
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMsgList();
            }
        });
        //加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
            }
        }, mRvMsgList);
        mPresenter.init();
    }

    @Override
    public void showMsgList(List<ISimilarityContractMsgItem> list) {
        mAdapter.setNewData(list);
    }

    @Override
    public void enableRefresh() {
        mRefreshLayout.setEnableRefresh(true);
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void showInitLoading() {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataLoading();
    }

    @Override
    public void showInitFailed(String msg) {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataFail(msg, new View.OnClickListener() {
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
    public void showDataEmpty() {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataEmpty("");
    }

    @Override
    public void scrollToBottom() {
        mRvMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showLoadMoreEnd() {
        mAdapter.loadMoreEnd();
    }


}
