package com.hm.iou.msg.business.friendlist.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.friendlist.FriendListContract;
import com.hm.iou.msg.business.friendlist.FriendListPresenter;
import com.hm.iou.msg.widget.SideLetterBar;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class FriendListActivity extends BaseActivity<FriendListPresenter> implements FriendListContract.View {

    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;
    @BindView(R2.id.side_letter_bar)
    SideLetterBar mSideLetterBar;

    FriendSectionAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mMoving = false;
    private int mIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_friend_list;
    }

    @Override
    protected FriendListPresenter initPresenter() {
        return new FriendListPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mAdapter = new FriendSectionAdapter(mContext);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRvMsgList.setLayoutManager(mLinearLayoutManager);
        mRvMsgList.setAdapter(mAdapter);
        mRvMsgList.addOnScrollListener(new RecyclerViewListener());
        mAdapter.setOnItemChildClickListener(new BaseSectionQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FriendSection item = (FriendSection) adapter.getItem(position);
                if (view.getId() == R.id.ll_friend_item && !item.isHeader) {
                    IFriend data = item.t;
                    Logger.d("Pinyin: " + data.getPinyin());
                    //跳转到好友详情页面
                    NavigationHelper.toFriendDetailPage(FriendListActivity.this, data.getIAccount());
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

        mSideLetterBar.setOverlay((TextView) findViewById(R.id.tv_letter_overlay));
        mSideLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                Integer index = mAdapter.getHeaderIndex(letter);
                if (index != null) {
                    Logger.d("Scroll to index = " + index);
                    moveToPosition(index);
                }
            }
        });

        mPresenter.init();
    }

    @Override
    public void showMsgList(List<FriendSection> list) {
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
        mLoadingInit.showDataEmpty("啊哦，空空如也~");
    }

    private void moveToPosition(int n) {
        mIndex = n;
        mRvMsgList.stopScroll();
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRvMsgList.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRvMsgList.getChildAt(n - firstItem).getTop();
            mRvMsgList.scrollBy(0, top);
        } else {
            mRvMsgList.scrollToPosition(n);
            mMoving = true;
        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mMoving && newState == RecyclerView.SCROLL_STATE_IDLE) {
                mMoving = false;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRvMsgList.getChildCount()) {
                    int top = mRvMsgList.getChildAt(n).getTop();
                    mRvMsgList.smoothScrollBy(0, top);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mMoving) {
                mMoving = false;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRvMsgList.getChildCount()) {
                    int top = mRvMsgList.getChildAt(n).getTop();
                    mRvMsgList.scrollBy(0, top);
                }
            }
        }
    }

}