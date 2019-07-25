package com.hm.iou.msg.business.apply.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.CircleImageView;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.PullDownRefreshImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.hm.iou.msg.R2.id.iv_qr_code;

public class ApplyNewFriendListActivity extends BaseActivity<ApplyNewFriendListPresenter> implements ApplyNewFriendListContract.View {

    @BindView(R2.id.iv_msg_refresh)
    PullDownRefreshImageView mIvMsgRefresh;
    @BindView(R2.id.rv_msgList)
    RecyclerView mRvMsgList;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.iv_header)
    CircleImageView mIvHeader;
    @BindView(R2.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R2.id.tv_show_id)
    TextView mTvShowId;
    @BindView(iv_qr_code)
    ImageView mIvQrCode;
    @BindView(R2.id.bottomBar)
    HMBottomBarView mBottomBar;

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
        mBottomBar.setOnTitleClickListener(new HMBottomBarView.OnTitleClickListener() {
            @Override
            public void onClickTitle() {
                Router.getInstance()
                        .buildWithUrl("hmiou://m.54jietiao.com/person/set_type_of_add_friend_by_other")
                        .navigation(mContext);
            }
        });

        mAdapter = new ApplyNewFriendListAdapter(mContext);
        mRvMsgList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgList.setAdapter(mAdapter);

        HMLoadingView load = new HMLoadingView(mContext);
        load.showDataEmpty("");
        mAdapter.setEmptyView(load);
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
        //设置下拉刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMsgList();
            }
        });

        mPresenter.init();
    }


    @OnClick({R2.id.iv_qr_code, R2.id.ll_search, R2.id.ll_mobile_contract, R2.id.ll_sweep_qr_code, R2.id.ll_add_my_self})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.ll_search == id) {
            NavigationHelper.toAddNewFriend(this);
        } else if (R.id.ll_sweep_qr_code == id) {
            Router.getInstance()
                    .buildWithUrl("hmiou://m.54jietiao.com/qrcode/index")
                    .withString("show_type", "show_scan_code")
                    .navigation(mContext);
        } else if (R.id.ll_add_my_self == id || R.id.iv_qr_code == id) {
            NavigationHelper.toAddMySelf(mContext);
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
    public void enableRefresh(boolean enabled) {
        mRefreshLayout.setEnableRefresh(enabled);
    }

    @Override
    public void hidePullDownRefresh() {
        mRefreshLayout.finishRefresh();
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
        ImageLoader.getInstance(mContext).displayImage(headerUrl, mIvHeader, R.drawable.uikit_bg_pic_loading_place, R.mipmap.uikit_icon_header_unknow);
        mTvNickname.setText(nickName);
        mTvShowId.setText("ID：" + showId);
    }

    @Override
    public void showQRCodeImage(Bitmap bitmap) {
        mIvQrCode.setImageBitmap(bitmap);
    }

}
