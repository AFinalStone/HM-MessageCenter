package com.hm.iou.msg.business.friend.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.friend.FriendDetailContract;
import com.hm.iou.msg.business.friend.presenter.FriendDetailPresenter;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.dialog.HMActionSheetDialog;
import com.hm.iou.uikit.dialog.HMAlertDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hjy on 2019/4/11.
 */

public class FriendDetailActivity extends BaseActivity<FriendDetailPresenter> implements FriendDetailContract.View {

    public static final String EXTRA_KEY_USER_ID = "userId";
    //值为2时，表示im账户，其他表示普通用户id
    public static final String EXTRA_KEY_ID_TYPE = "idType";

    //申请状态，1：表示好友申请已过期，2：等待确认好友申请，除了从好友申请列表页进来需要此参数外，其他均不需要考虑
    public static final String EXTRA_KEY_APPLY_STATUS = "applyStatus";
    //对方申请加你为好友时的备注信息
    public static final String EXTRA_KEY_COMMENT_INFO = "comment";

    public static final String APPLY_OVERDUE = "1";
    public static final String APPLY_WAIT_CONFIRM = "2";

    public static final int ID_TYPE_IM = 2; //表示传入的id类型为 im 账户Id

    public static final int REQ_SEND_VERIFY_REQUEST = 100;

    @BindView(R2.id.ll_friend_bottom)
    HMBottomBarView mBottomBarView;
    @BindView(R2.id.iv_friend_avatar)
    ImageView mIvAvatar;
    @BindView(R2.id.tv_friend_name1)
    TextView mTvName1;
    @BindView(R2.id.tv_friend_name2)
    TextView mTvName2;
    @BindView(R2.id.tv_friend_id)
    TextView mTvId;
    @BindView(R2.id.tv_friend_type)
    TextView mTvUserType;               //用户类型
    @BindView(R2.id.tv_friend_city)
    TextView mTvCity;                   //城市
    @BindView(R2.id.ll_friend_tips)
    LinearLayout mLlTips;                   //
    @BindView(R2.id.btn_friend_submit)
    Button mBtnSubmit;
    @BindView(R2.id.ll_friend_comment)
    LinearLayout mLlCommentInfo;
    @BindView(R2.id.tv_friend_comment)
    TextView mTvCommentInfo;

    @BindView(R2.id.rl_friend_content)
    RelativeLayout mRlContent;
    @BindView(R2.id.loading_view)
    HMLoadingView mLoadingView;

    private String mUserId;
    private int mIdType;

    private String mApplyStatus;        //好友申请状态，确认好友时，需要传此参数，其他状态不考虑
    private String mCommentInfo;

    private AddRemarkNameDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_friend_detail;
    }

    @Override
    protected FriendDetailPresenter initPresenter() {
        return new FriendDetailPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        Intent data = getIntent();
        mUserId = data.getStringExtra(EXTRA_KEY_USER_ID);
        mIdType = data.getIntExtra(EXTRA_KEY_ID_TYPE, 0);
        mApplyStatus = getIntent().getStringExtra(EXTRA_KEY_APPLY_STATUS);
        mCommentInfo = getIntent().getStringExtra(EXTRA_KEY_COMMENT_INFO);
        if (bundle != null) {
            mUserId = bundle.getString(EXTRA_KEY_USER_ID);
            mIdType = bundle.getInt(EXTRA_KEY_ID_TYPE);
            mApplyStatus = bundle.getString(EXTRA_KEY_APPLY_STATUS);
            mCommentInfo = bundle.getString(EXTRA_KEY_COMMENT_INFO);
        }
        if (!TextUtils.isEmpty(mApplyStatus) && !TextUtils.isEmpty(mCommentInfo)) {
            mLlCommentInfo.setVisibility(View.VISIBLE);
            mTvCommentInfo.setText(mCommentInfo);
        }

        mBottomBarView.setOnTitleIconClickListener(new HMBottomBarView.OnTitleIconClickListener() {
            @Override
            public void onClickIcon() {
                mPresenter.clickBottomMore();
            }
        });

        mPresenter.getUserInfo(mUserId, mIdType, mApplyStatus);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_USER_ID, mUserId);
        outState.putInt(EXTRA_KEY_ID_TYPE, mIdType);
        outState.putString(EXTRA_KEY_APPLY_STATUS, mApplyStatus);
        outState.putString(EXTRA_KEY_COMMENT_INFO, mCommentInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SEND_VERIFY_REQUEST) {
            if (resultCode == RESULT_OK) {
                //如果申请好友时，已经是好友了，需要重新刷新一下数据
                mPresenter.getUserInfo(mUserId, mIdType, null);
            }
        }
    }

    @OnClick(value = {R2.id.btn_friend_submit})
    void onClick(View v) {
        if (v.getId() == R.id.btn_friend_submit) {
            mPresenter.clickSubmitButton();
        }
    }

    @Override
    public void showDetailLoading() {
        mLoadingView.showDataLoading();
    }

    @Override
    public void hideDetailLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showDetailContent(int visibility) {
        mRlContent.setVisibility(visibility);
    }

    @Override
    public void showDetailError(String errMsg) {
        mLoadingView.showDataFail(errMsg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getUserInfo(mUserId, mIdType, mApplyStatus);
            }
        });
    }

    @Override
    public void showAvatar(String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            mIvAvatar.setImageResource(R.mipmap.uikit_icon_header_unknow);
            return;
        }
        ImageLoader.getInstance(this).displayImage(avatar, mIvAvatar, R.mipmap.uikit_icon_header_unknow,
                R.mipmap.uikit_icon_header_unknow);
    }

    @Override
    public void showNickname(String nickname, String remarkName) {
        if (TextUtils.isEmpty(remarkName)) {
            mTvName2.setVisibility(View.GONE);
            mTvName1.setText(nickname);
        } else {
            mTvName2.setVisibility(View.VISIBLE);
            mTvName1.setText(remarkName);
            mTvName2.setText("昵称：" + nickname);
        }
    }

    @Override
    public void showUserId(String showId) {
        mTvId.setText("ID：" + showId);
    }

    @Override
    public void showUserType(String userType) {
        mTvUserType.setText(userType);
    }

    @Override
    public void showLocation(String city) {
        mTvCity.setText(city);
    }

    @Override
    public void showBlackNameTips(int visibility) {
        mLlTips.setVisibility(visibility);
    }

    @Override
    public void showButtonText(String text) {
        mBtnSubmit.setText(text);
    }

    @Override
    public void showApplyCommentInfo(int visibility) {
        mLlCommentInfo.setVisibility(visibility);
    }

    @Override
    public void showActionSheet(List<String> list) {
        new HMActionSheetDialog.Builder(this)
                .setActionSheetList(list)
                .setOnItemClickListener(new HMActionSheetDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i, String s) {
                        mPresenter.clickAction(s);
                    }
                }).create().show();
    }

    @Override
    public void showAlertDialog(String msg) {
        new HMAlertDialog.Builder(this)
                .setMessage(msg)
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton("确定")
                .create().show();
    }

    @Override
    public void showBlackNameConfirmDialog() {
        new HMAlertDialog.Builder(this)
                .setMessage("加入黑名单后，您将不再收到对方的消息")
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        mPresenter.addBlackName();
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create().show();
    }

    @Override
    public void showUnbindFriendRelationConfirmDialog(String msg) {
        new HMAlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        mPresenter.removeFriendRelationship();
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create().show();
    }

    @Override
    public void showAddRemarkNameDialog() {
        if (mDialog == null) {
            mDialog = new AddRemarkNameDialog(this);
            mDialog.setOnModifyNameListener(new AddRemarkNameDialog.OnModifyNameListener() {
                @Override
                public void onClickSend(String content) {
                    mPresenter.updateRemarkName(content);
                }
            });
        }
        mDialog.show();
    }

    @Override
    public void showFriendApplyOverdueDialog() {
        new HMAlertDialog.Builder(this)
                .setMessage("朋友信息已过期，请主动添加为好友")
                .setPositiveButton("添加")
                .setNegativeButton("取消")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                        NavigationHelper.toSendVerifyRequestPage(FriendDetailActivity.this, mUserId, REQ_SEND_VERIFY_REQUEST);
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create().show();

    }
}