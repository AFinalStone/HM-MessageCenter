package com.hm.iou.msg.business.alipay.list.view;

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
import com.hm.iou.msg.business.alipay.list.AliPayMsgContract;
import com.hm.iou.msg.business.alipay.list.AliPayMsgPresenter;
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

public class AliPayMsgActivity extends BaseActivity<AliPayMsgPresenter> implements AliPayMsgContract.View {

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

    AliPayListAdapter mAdapter;
    HMAlertDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_alipay_list;
    }

    @Override
    protected AliPayMsgPresenter initPresenter() {
        return new AliPayMsgPresenter(this, this);
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
                                    mPresenter.makeTypeMsgHaveRead();
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
        mAdapter = new AliPayListAdapter(mContext);
        mAdapter.setLoadMoreView(new HMLoadMoreView());
        mAdapter.setHeaderAndEmpty(true);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                IAliPayMsgItem item = (IAliPayMsgItem) adapter.getItem(position);
                if (item != null) {
                    if (!item.isHaveRead()) {
                        mPresenter.makeSingleMsgHaveRead(item, position);
                    }
                    NavigationHelper.toAliPayMsgDetailPage(mContext, item);
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
        //设置底部刷新,这里不设置不会出现底部底线的文案
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        }, mRvMsgList);
        mPresenter.init();
    }

    @Override
    public void showMsgList(List<IAliPayMsgItem> list) {
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

    @Override
    public void notifyItem(IAliPayMsgItem item, int position) {
        mAdapter.setData(position, item);
    }

    @Override
    public void setBottomClearIconVisible(boolean isShow) {
        mBottomBar.setTitleIconVisible(isShow);
    }


}
