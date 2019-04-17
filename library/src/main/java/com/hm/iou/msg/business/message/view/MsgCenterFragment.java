package com.hm.iou.msg.business.message.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseFragment;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.business.message.MsgCenterContract;
import com.hm.iou.msg.business.message.MsgCenterPresenter;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.tools.StatusBarUtil;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MsgCenterFragment extends BaseFragment<MsgCenterPresenter> implements MsgCenterContract.View {

    @BindView(R2.id.view_statusbar_placeholder)
    View mViewStatusBar;
    @BindView(R2.id.tv_num_no_read)
    TextView mTvNumNoRead;
    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;

    HeaderViewHelper mHeaderViewHelper;
    ChatMsgListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_fragment_msg_center;
    }

    @Override
    protected MsgCenterPresenter initPresenter() {
        return new MsgCenterPresenter(mActivity, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(mActivity);
        if (statusBarHeight > 0) {
            ViewGroup.LayoutParams params = mViewStatusBar.getLayoutParams();
            params.height = statusBarHeight;
            mViewStatusBar.setLayoutParams(params);
        }

        //设置状态栏颜色为黑色
        com.hm.iou.base.utils.StatusBarUtil.setStatusBarDarkFont(mActivity, true);

        mRvMsgList.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ChatMsgListAdapter(mActivity);
        mHeaderViewHelper = new HeaderViewHelper(mRvMsgList, mPresenter);
        mAdapter.addHeaderView(mHeaderViewHelper.getRootView());
        mAdapter.bindToRecyclerView(mRvMsgList);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ChatMsgBean item = (ChatMsgBean) adapter.getItem(position);
                Logger.d("会话id==" + item.getContactId());
                if (R.id.rl_content == view.getId()) {
                    NavigationHelper.toSessionDetail(mActivity, item.getContactId());
                } else if (R.id.btn_hide == view.getId()) {
                    mPresenter.deleteItemByPosition(position);
                }
            }
        });
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshData();
            }
        });

        mPresenter.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mPresenter != null) {
            mPresenter.getRedFlagCount();
        }
        if (!hidden) {
            com.hm.iou.base.utils.StatusBarUtil.setStatusBarDarkFont(mActivity, true);
        }
    }

    @OnClick({R2.id.rl_header, R2.id.tv_right_title})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.rl_header == id) {
            EventBus.getDefault().post(new CommBizEvent("iou_show_home_left_menu", "显示首页左侧菜单"));
        } else if (R.id.tv_right_title == id) {
            NavigationHelper.toFriendList(mActivity);
        }
    }

    @Override
    public void showInitLoading() {
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataLoading();
    }

    @Override
    public void hideInitLoading() {
        mLoadingInit.setVisibility(View.GONE);
    }

    @Override
    public void updateRedFlagCount(String redFlagCount) {
        if (TextUtils.isEmpty(redFlagCount) || "0".equals(redFlagCount)) {
            mTvNumNoRead.setVisibility(View.INVISIBLE);
        } else {
            mTvNumNoRead.setVisibility(View.VISIBLE);
            mTvNumNoRead.setText(redFlagCount);
        }
    }

    @Override
    public void showMsgList(List<ChatMsgBean> list) {
        mAdapter.setNewData(list);
        if (list != null && !list.isEmpty()) {
            mLoadingInit.setVisibility(View.GONE);
        }
    }

    @Override
    public void showHeaderModule(List<MsgListHeaderBean> list) {
        if (mHeaderViewHelper != null) {
            mHeaderViewHelper.clearHeaderModules();
            mHeaderViewHelper.addModule(list);
        }
    }

    @Override
    public void refreshHeaderModule(MsgListHeaderBean msgListHeaderBean) {
        if (mHeaderViewHelper != null) {
            mHeaderViewHelper.updateModuleItem(msgListHeaderBean);
        }
    }

    @Override
    public void enableRefresh() {
        mRefreshLayout.setEnableRefresh(true);
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
    }


}
