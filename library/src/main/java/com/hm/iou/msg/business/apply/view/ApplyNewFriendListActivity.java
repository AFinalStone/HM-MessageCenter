package com.hm.iou.msg.business.apply.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.apply.ApplyNewFriendListContract;
import com.hm.iou.msg.business.apply.ApplyNewFriendListPresenter;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.dict.ApplyNewFriendStatus;
import com.hm.iou.router.Router;
import com.hm.iou.tools.DensityUtil;
import com.hm.iou.uikit.HMDotTextView;
import com.hm.iou.uikit.decoration.listener.PowerGroupListener;
import com.hm.iou.uikit.decoration.view.PowerfulStickyDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ApplyNewFriendListActivity extends BaseActivity<ApplyNewFriendListPresenter> implements ApplyNewFriendListContract.View {

    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.dot_red_msg_num)
    HMDotTextView mTvRedDot;

    ApplyNewFriendListAdapter mAdapter;
    HeaderHelper mHeaderHelper;

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
        mAdapter = new ApplyNewFriendListAdapter(mContext);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);
        //头部
        View viewHeader = LayoutInflater.from(mContext).inflate(R.layout.msgcenter_layout_apply_new_friend_list_header, null);
        mHeaderHelper = new HeaderHelper(mContext, viewHeader);
        mAdapter.addHeaderView(viewHeader);

        //悬浮条目
        PowerfulStickyDecoration pfd = PowerfulStickyDecoration.Builder.init(new PowerGroupListener() {
            @Override
            public View getGroupTitleView(int position) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.msgcenter_layout_apply_new_friend_list_header_sticty, null);
                return view;
            }

            @Override
            public String getGroupName(int position) {
                return "";
            }
        }).setHeaderCount(mAdapter.getHeaderLayoutCount())
                .setGroupTitleHeight(DensityUtil.dip2px(mContext, 24))
                .setGroupTitleBackground(Color.parseColor("#FFF8F8F9"))
                .build();
        mRvMsgList.addItemDecoration(pfd);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                IApplyNewFriend data = (IApplyNewFriend) adapter.getItem(position);
                if (data == null)
                    return;
                if (view.getId() == R.id.btn_waite_query || view.getId() == R.id.rl_content) {
                    int status = data.getIStatus();
                    String applyStatus = null;
                    if (status == ApplyNewFriendStatus.NEED_TO_AGREE.getValue()) {
                        applyStatus = FriendDetailActivity.APPLY_WAIT_CONFIRM;
                    } else if (status == ApplyNewFriendStatus.HAVE_OVER.getValue()) {
                        applyStatus = FriendDetailActivity.APPLY_OVERDUE;
                    }
                    NavigationHelper.toFriendConfirmPage(ApplyNewFriendListActivity.this,
                            data.getFriendId(), applyStatus, data.getApplyId(), data.getStageName());
                } else if (view.getId() == R.id.btn_delete) {
                    String applyId = data.getApplyId();
                    mPresenter.deleteApplyRecord(applyId);
                }
            }
        });

        mPresenter.init();
    }


    @OnClick({R2.id.iv_bottom_more, R2.id.ll_bottom_back})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_bottom_more == id) {
            Router.getInstance()
                    .buildWithUrl("hmiou://m.54jietiao.com/person/set_type_of_add_friend_by_other")
                    .navigation(mContext);
        } else if (R.id.ll_bottom_back == id) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void showMsgList(List<IApplyNewFriend> list) {
        mAdapter.setNewData(list);
    }


    @Override
    public void removeData(String applyId) {
        if (mAdapter != null) {
            mAdapter.removeData(applyId);
        }
    }

    @Override
    public void removeDataByFriendId(String friendId) {
        if (mAdapter != null) {
            mAdapter.removeDataByFriendId(friendId);
        }
    }

    @Override
    public void showHeaderData(String headerUrl, String nickName, String showId) {
        if (mHeaderHelper != null) {
            mHeaderHelper.showHeaderData(headerUrl, nickName, showId);
        }
    }

    @Override
    public void showSex(int sexImageResId) {
        if (mHeaderHelper != null) {
            mHeaderHelper.showSex(sexImageResId);
        }
    }


    @Override
    public void showQRCodeImage(Bitmap bitmap) {
        if (mHeaderHelper != null) {
            mHeaderHelper.showQRCodeImage(bitmap);
        }
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

}
