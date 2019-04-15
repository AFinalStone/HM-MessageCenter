package com.hm.iou.msg.business.apply.view;

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
import com.hm.iou.msg.business.apply.ApplyNewFriendListContract;
import com.hm.iou.msg.business.apply.ApplyNewFriendListPresenter;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.dict.ApplyNewFriendStatus;
import com.hm.iou.tools.StatusBarUtil;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class ApplyNewFriendListActivity extends BaseActivity<ApplyNewFriendListPresenter> implements ApplyNewFriendListContract.View {

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

    ApplyNewFriendListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_apply_new_friend_list;
    }

    @Override
    protected ApplyNewFriendListPresenter initPresenter() {
        return new ApplyNewFriendListPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
        if (statusBarHeight > 0) {
            ViewGroup.LayoutParams params = mViewStatusBar.getLayoutParams();
            params.height = statusBarHeight;
            mViewStatusBar.setLayoutParams(params);
        }

        mAdapter = new ApplyNewFriendListAdapter(mContext);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                IApplyNewFriend data = (IApplyNewFriend) adapter.getItem(position);
                if (data == null)
                    return;
                if (view.getId() == R.id.rl_content) {
                    int status = data.getIStatus();
                    String applyStatus = null;
                    if (status == ApplyNewFriendStatus.NEED_TO_AGREE.getValue()) {
                        applyStatus = FriendDetailActivity.APPLY_WAIT_CONFIRM;
                    } else if (status == ApplyNewFriendStatus.HAVE_OVER.getValue()) {
                        applyStatus = FriendDetailActivity.APPLY_OVERDUE;
                    }
                    NavigationHelper.toFriendDetailPage(ApplyNewFriendListActivity.this,
                            data.getFriendId(), applyStatus, data.getIContent());
                } else if (view.getId() == R.id.btn_delete) {
                    String applyId = data.getApplyId();
                    mPresenter.deleteApplyRecord(applyId);
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

        mPresenter.init();
    }

    @Override
    public void showMsgList(List<IApplyNewFriend> list) {
        mAdapter.setNewData(list);
    }

    @Override
    public void enableRefresh(boolean enabled) {
        mRefreshLayout.setEnableRefresh(enabled);
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
    public void showInitFailed() {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataFail(new View.OnClickListener() {
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
    public void removeData(String applyId) {
        if (mAdapter != null) {
            mAdapter.removeData(applyId);
        }
    }
}
