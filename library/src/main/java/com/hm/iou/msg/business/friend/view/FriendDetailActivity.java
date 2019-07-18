package com.hm.iou.msg.business.friend.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.FriendDbUtil;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.friend.FriendDetailContract;
import com.hm.iou.msg.business.friend.presenter.FriendDetailPresenter;
import com.hm.iou.msg.dict.IdType;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.dialog.HMActionSheetDialog;
import com.hm.iou.uikit.dialog.HMAlertDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hjy on 2019/4/11.
 */

public class FriendDetailActivity extends BaseActivity<FriendDetailPresenter> implements FriendDetailContract.View {

    //用户的userId或者是云信账户的imAccId
    public static final String EXTRA_KEY_USER_ID = "userId";
    //值为2时，表示im账户，其他表示普通用户id
    public static final String EXTRA_KEY_ID_TYPE = "idType";

    //申请状态，1：表示好友申请已过期，2：等待确认好友申请，除了从好友申请列表页进来需要此参数外，其他均不需要考虑
    public static final String EXTRA_KEY_APPLY_STATUS = "applyStatus";
    public static final String EXTRA_KEY_APPLY_ID = "applyId";
    public static final String EXTRA_KEY_REMARK_NAME = "remarkName";        //好友申请时的备注名

    public static final String APPLY_OVERDUE = "1";
    public static final String APPLY_WAIT_CONFIRM = "2";

    //发送好友请求
    public static final int REQ_SEND_VERIFY_REQUEST = 100;
    public static final int REQ_AGREE_ADD_FRIEND = 101;

    @BindView(R2.id.ll_friend_bottom)
    HMBottomBarView mBottomBarView;
    @BindView(R2.id.iv_friend_avatar)
    ImageView mIvAvatar;
    @BindView(R2.id.iv_friend_sex)
    ImageView mIvSex;                       //性别
    @BindView(R2.id.tv_friend_reamrk_name)
    TextView mTvRemarkName;                 //备注名
    @BindView(R2.id.tv_friend_id)
    TextView mTvId;
    @BindView(R2.id.tv_friend_type)
    TextView mTvUserType;                   //用户类型
    @BindView(R2.id.tv_friend_city)
    TextView mTvCity;                       //城市
    @BindView(R2.id.btn_friend_submit)
    Button mBtnSubmit;
    @BindView(R2.id.btn_friend_refuse)
    Button mBtnRefuse;                      //拒绝
    @BindView(R2.id.rl_friend_comment)
    RelativeLayout mRlCommentInfo;
    @BindView(R2.id.tv_friend_comment)
    TextView mTvCommentInfo;                //下面的备注名

    @BindView(R2.id.rl_friend_content)
    RelativeLayout mRlContent;
    @BindView(R2.id.loading_view)
    HMLoadingView mLoadingView;

    private String mUserId;     //用户id
    private int mIdType;        //id类型，值为2时，表示im账户，其他都表示普通userId

    private String mApplyStatus;        //好友申请状态，确认好友时，需要传此参数，其他状态不考虑
    private String mApplyId;            //好友申请记录id
    private String mApplyRemarkName;    //好友申请时的备注名

    private AddRemarkNameDialog mAddRemarkDialog;

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
        mIdType = data.getIntExtra(EXTRA_KEY_ID_TYPE, IdType.COMM.type);
        mApplyStatus = data.getStringExtra(EXTRA_KEY_APPLY_STATUS);
        mApplyId = data.getStringExtra(EXTRA_KEY_APPLY_ID);
        mApplyRemarkName = data.getStringExtra(EXTRA_KEY_REMARK_NAME);
        if (bundle != null) {
            mUserId = bundle.getString(EXTRA_KEY_USER_ID);
            mIdType = bundle.getInt(EXTRA_KEY_ID_TYPE, IdType.COMM.type);
            mApplyStatus = bundle.getString(EXTRA_KEY_APPLY_STATUS);
            mApplyId = bundle.getString(EXTRA_KEY_APPLY_ID);
            mApplyRemarkName = bundle.getString(EXTRA_KEY_REMARK_NAME);
        }

        mBottomBarView.setOnTitleIconClickListener(new HMBottomBarView.OnTitleIconClickListener() {
            @Override
            public void onClickIcon() {
                mPresenter.clickBottomMore();
            }
        });

        mPresenter.setFriendConfirmFlag(mApplyStatus, mApplyId, mApplyRemarkName);
        mPresenter.getUserInfo(mUserId, mIdType);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_USER_ID, mUserId);
        outState.putInt(EXTRA_KEY_ID_TYPE, mIdType);
        outState.putString(EXTRA_KEY_APPLY_STATUS, mApplyStatus);
        outState.putString(EXTRA_KEY_APPLY_ID, mApplyId);
        outState.putString(EXTRA_KEY_REMARK_NAME, mApplyRemarkName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SEND_VERIFY_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPresenter.getUserInfo(mUserId, mIdType);
            }
        } else if (requestCode == REQ_AGREE_ADD_FRIEND) {
            if (resultCode == RESULT_OK) {
                mPresenter.getUserInfo(mUserId, mIdType);
            }
        }
    }

    @OnClick(value = {R2.id.btn_friend_submit, R2.id.rl_friend_comment, R2.id.btn_friend_refuse})
    void onClick(View v) {
        if (v.getId() == R.id.btn_friend_submit) {
            mPresenter.clickSubmitButton();
        } else if (v.getId() == R.id.rl_friend_comment) {
            showAddRemarkNameDialog();
        } else if (v.getId() == R.id.btn_friend_refuse) {
            mPresenter.refuseFriend(mApplyId);
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
    public void showBottomMore(boolean isShow) {
        mBottomBarView.setTitleIconVisible(isShow);
    }

    @Override
    public void showDetailError(String errMsg) {
        mLoadingView.showDataFail(errMsg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getUserInfo(mUserId, mIdType);
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
    public void showSexImage(int sexResId) {
        if (sexResId <= 0) {
            mIvSex.setVisibility(View.INVISIBLE);
        } else {
            mIvSex.setVisibility(View.VISIBLE);
            mIvSex.setImageResource(sexResId);
        }
    }

    @Override
    public void showNickname(String nickname, String remarkName) {
        mTvRemarkName.setText(TextUtils.isEmpty(remarkName) ? nickname : remarkName);
    }

    @Override
    public void showUserId(String showId) {
        mTvId.setText(showId);
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
    public void showCommentNameView(int visibility, String name) {
        mRlCommentInfo.setVisibility(visibility);
        mTvCommentInfo.setText(name);
    }

    @Override
    public void showButtonText(String text) {
        mBtnSubmit.setText(text);
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
                .setTitle("拉黑并删除用户")
                .setMessage("如果有债务纠纷，请不要意气用事，有话好好沟通。")
                .setPositiveButton("再考虑一下")
                .setNegativeButton("拉黑并删除")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                    }

                    @Override
                    public void onNegClick() {
                        mPresenter.addBlackName();
                    }
                })
                .create().show();
    }

    @Override
    public void showUnbindFriendRelationConfirmDialog(String msg) {
        new HMAlertDialog.Builder(this)
                .setTitle("删除好友")
                .setMessage(msg)
                .setPositiveButton("取消")
                .setNegativeButton("删除")
                .setOnClickListener(new HMAlertDialog.OnClickListener() {
                    @Override
                    public void onPosClick() {
                    }

                    @Override
                    public void onNegClick() {
                        mPresenter.removeFriendRelationship();
                    }
                })
                .create().show();
    }

    @Override
    public void showAddRemarkNameDialog() {
        if (mAddRemarkDialog == null) {
            mAddRemarkDialog = new AddRemarkNameDialog(this);
            mAddRemarkDialog.setOnModifyNameListener(new AddRemarkNameDialog.OnModifyNameListener() {
                @Override
                public void onClickSend(String content) {
                    mPresenter.updateRemarkName(content);
                }
            });
        }
        mAddRemarkDialog.show();
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
                        NavigationHelper.toSendVerifyRequestPage(FriendDetailActivity.this, mUserId, true, null, REQ_SEND_VERIFY_REQUEST);
                    }

                    @Override
                    public void onNegClick() {

                    }
                })
                .create().show();
    }

    @Override
    public void showRefuseBtn(int visibility) {
        mBtnRefuse.setVisibility(visibility);
    }
}