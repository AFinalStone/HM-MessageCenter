package com.hm.iou.msg.business.contractmsg.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.contractmsg.ContractMsgContract;
import com.hm.iou.msg.business.contractmsg.ContractMsgPresenter;
import com.hm.iou.uikit.HMDotTextView;
import com.hm.iou.uikit.HMLoadMoreView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.HMTopBarView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.hm.iou.uikit.dialog.HMActionSheetDialog;
import com.hm.iou.uikit.dialog.HMAlertDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ContractMsgActivity extends BaseActivity<ContractMsgPresenter> implements ContractMsgContract.View {

    @BindView(R2.id.topbar)
    HMTopBarView mTopBarView;
    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;
    @BindView(R2.id.iv_bottom_more)
    ImageView mIvMore;
    @BindView(R2.id.dot_chat_red_msg_num)
    HMDotTextView mTvRedDot;

    ContractMsgListAdapter mAdapter;

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
        mTopBarView.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
            @Override
            public void onClickTextMenu() {
                showRemindRuleDialog();
            }

            @Override
            public void onClickImageMenu() {

            }
        });

        mAdapter = new ContractMsgListAdapter(mContext);
        mAdapter.setLoadMoreView(new HMLoadMoreView());
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                IContractMsgItem item = (IContractMsgItem) adapter.getItem(position);
                if (item == null)
                    return;
                if (view.getId() == R.id.btn_delete) {
                    mPresenter.deleteMsg(item);
                } else if(view.getId() == R.id.rl_content) {
                    if (!item.isHaveRead()) {
                        mPresenter.makeSingleMsgHaveRead(item, position);
                    }
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
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        }, mRvMsgList);
        mPresenter.init();
    }

    @OnClick(value = {R2.id.iv_bottom_more, R2.id.ll_bottom_back})
    void onClick(View v) {
        if (v.getId() == R.id.iv_bottom_more) {
            List<String> list = new ArrayList<>();
            list.add("全部标为已读");
            list.add("清空已读数据");
            new HMActionSheetDialog.Builder(this)
                    .setActionSheetList(list)
                    .setOnItemClickListener(new HMActionSheetDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(int i, String s) {
                            if (i == 0) {
                                mPresenter.makeTypeMsgHaveRead();
                            } else if (i == 1) {
                                showClearConfirmDialog();
                            }
                        }
                    })
                    .create().show();
        } else if (v.getId() == R.id.ll_bottom_back) {
            finish();
        }
    }

    private void showClearConfirmDialog() {
        new HMAlertDialog.Builder(this)
                .setMessage("是否清空全部已读消息")
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        mPresenter.clearAllReadData();
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create().show();
    }

    @Override
    public void showMsgList(List<IContractMsgItem> list) {
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
        showMsgList(null);
        mLoadingInit.setVisibility(View.VISIBLE);
        mLoadingInit.showDataEmpty("没有消息或者消息已清空", R.mipmap.uikit_data_bell, "提醒规则", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemindRuleDialog();
            }
        });
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
    public void setBottomMoreIconVisible(boolean isShow) {
        mIvMore.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showRedDot(int c) {
        if (c > 0) {
            mTvRedDot.setText(c > 99 ? "···" : c + "");
            mTvRedDot.setVisibility(View.VISIBLE);
        } else {
            mTvRedDot.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeData(String msgId) {
        if (mAdapter != null) {
            mAdapter.removeDataByMsgId(msgId);
            if (mAdapter.getData().isEmpty()) {
                showDataEmpty();
                setBottomMoreIconVisible(false);
            }
        }
    }

    @Override
    public void updateData(IContractMsgItem msgItem) {
        if (mAdapter != null) {
            mAdapter.updateData(msgItem);
        }
    }

    /**
     * 显示提醒规则弹窗
     */
    private void showRemindRuleDialog() {
        new HMAlertDialog.Builder(this)
                .setTitle(R.string.messagecenter_remind_rule)
                .setMessage(R.string.messagecenter_rule_contract)
                .setPositiveButton(R.string.messagecenter_i_know)
                .create().show();
    }

}
