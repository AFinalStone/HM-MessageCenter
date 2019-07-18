package com.hm.iou.msg.business.friend.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendRelationResBean;
import com.hm.iou.msg.dict.IdType;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.StringUtil;
import com.hm.iou.tools.ToastUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hjy on 2019/4/12.
 */

public class SendVerifyRequestActivity extends BaseActivity {

    public static final String EXTRA_KEY_FRIEND_ID = "friend_id";
    public static final String EXTRA_KEY_ID_TYPE = "id_type";
    public static final String EXTRA_KEY_IS_ADD_FRIEND = "is_add_friend";
    public static final String EXTRA_KEY_FRIEND_INFO = "friend_info";       //好友详情信息

    @BindView(R2.id.et_verify_content)
    EditText mEtContent;
    @BindView(R2.id.btn_verify_send)
    Button mBtnSend;

    private String mFriendId;
    private int mIdType;
    private boolean mIsAddFriend;
    private FriendInfo mFriendInfo;

    private Disposable mAddFriendReq;
    private Disposable mAgreeReq;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_send_verify_request;
    }

    @Override
    protected MvpActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        Intent data = getIntent();
        mFriendId = data.getStringExtra(EXTRA_KEY_FRIEND_ID);
        mIdType = data.getIntExtra(EXTRA_KEY_ID_TYPE, IdType.COMM.type);
        mIsAddFriend = data.getBooleanExtra(EXTRA_KEY_IS_ADD_FRIEND, true);
        mFriendInfo = data.getParcelableExtra(EXTRA_KEY_FRIEND_INFO);
        if (bundle != null) {
            mFriendId = bundle.getString(EXTRA_KEY_FRIEND_ID);
            mIdType = bundle.getInt(EXTRA_KEY_ID_TYPE, IdType.COMM.type);
            mIsAddFriend = bundle.getBoolean(EXTRA_KEY_IS_ADD_FRIEND, true);
            mFriendInfo = bundle.getParcelable(EXTRA_KEY_FRIEND_INFO);
        }
        RxTextView.textChanges(mEtContent).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence sequence) throws Exception {
                mBtnSend.setEnabled(TextUtils.isEmpty(sequence) ? false : true);
            }
        });
        if (!mIsAddFriend) {
            mBtnSend.setText("提交");
        }

        UserInfo userInfo = UserManager.getInstance(this).getUserInfo();
        mEtContent.setText(StringUtil.getUnnullString(userInfo.getNickName()));
        mEtContent.setSelection(mEtContent.length());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId);
        outState.putInt(EXTRA_KEY_ID_TYPE, mIdType);
        outState.putBoolean(EXTRA_KEY_IS_ADD_FRIEND, mIsAddFriend);
        outState.putParcelable(EXTRA_KEY_FRIEND_INFO, mFriendInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelReq(mAddFriendReq);
        cancelReq(mAgreeReq);
    }

    @OnClick(value = {R2.id.btn_verify_send})
    void onClick(View v) {
        if (v.getId() == R.id.btn_verify_send) {
            String content = mEtContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                return;
            }
            if (mIsAddFriend) {
                sendVerifyRequest(content);
            } else {
                agreeApplyNewFriend();
            }
        }
    }

    private void cancelReq(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 发送好友添加请求
     *
     * @param content
     */
    private void sendVerifyRequest(String content) {
        showLoadingView();
        mAddFriendReq = MsgApi.addFriendRequest(mFriendId, content, mIdType)
                .map(RxUtil.<Boolean>handleResponse())
                .subscribeWith(new CommSubscriber<Boolean>(this) {
                    @Override
                    public void handleResult(Boolean result) {
                        dismissLoadingView();
                        ToastUtil.showStatusView(SendVerifyRequestActivity.this, "申请已发送");
                        EventBus.getDefault().post(new AddFriendEvent());
                        if (result != null && result) {
                            checkFriendRelation();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        dismissLoadingView();
                    }
                });
    }

    /**
     * 同意添加为好友
     */
    private void agreeApplyNewFriend() {
        showLoadingView();
        mAgreeReq = MsgApi.agreeApply(mFriendId, mEtContent.getText().toString().trim())
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(this) {
                    @Override
                    public void handleResult(Object o) {
                        dismissLoadingView();
                        EventBus.getDefault().post(new AddFriendEvent());
                        checkFriendRelation();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        dismissLoadingView();
                    }
                });
    }

    private void checkFriendRelation() {
        if (mFriendInfo == null) {
            IMHelper.getInstance(mContext).refreshTokenAndLogin();
            closePageSucc();
        } else {
            showLoadingView();
            MsgApi.findFriendRelation(mFriendInfo.getFriendId())
                    .map(RxUtil.<FriendRelationResBean>handleResponse())
                    .subscribeWith(new CommSubscriber<FriendRelationResBean>(this) {
                        @Override
                        public void handleResult(FriendRelationResBean data) {
                            dismissLoadingView();
                            int code = data.getRelationCode();
                            if (code == 4) {            //等待对方确认
                                IMHelper.getInstance(mContext).refreshTokenAndLogin();
                                NavigationHelper.toWaitProcessPage(mContext, mFriendInfo.getFriendId(),
                                        mFriendInfo.getSex(), mFriendInfo.getAvatarUrl(), data.getDesc(), data.isOverdue());
                                closePageSucc();
                            } else if (code == 5) {     //已经是好友
                                ensureImInit(data.getImAccid());
                            } else if (code == 6) {     //被对方拉黑了
                                IMHelper.getInstance(mContext).refreshTokenAndLogin();
                                NavigationHelper.toBlackNamePage(mContext, mFriendInfo.getFriendId(), data.getDesc(), true, mFriendInfo);
                                closePageSucc();
                            } else if (code == 7) {     //拉黑了对方
                                IMHelper.getInstance(mContext).refreshTokenAndLogin();
                                NavigationHelper.toBlackNamePage(mContext, mFriendInfo.getFriendId(), data.getDesc(), false, mFriendInfo);
                                closePageSucc();
                            } else if (code == 8) {     //对方已注销
                                IMHelper.getInstance(mContext).refreshTokenAndLogin();
                                NavigationHelper.toAccountClosedPage(mContext, mFriendInfo.getSex(), mFriendInfo.getAvatarUrl(), data.getDesc(), true);
                                closePageSucc();
                            } else {
                                IMHelper.getInstance(mContext).refreshTokenAndLogin();
                                closePageSucc();
                            }
                        }

                        @Override
                        public void handleException(Throwable t, String s, String s1) {
                            dismissLoadingView();
                            IMHelper.getInstance(mContext).refreshTokenAndLogin();
                            closePageSucc();
                        }
                    });
        }
    }

    /**
     * 确保云信SDK有初始化之后，再跳转到会话聊天页面
     *
     * @param imAccId
     */
    private void ensureImInit(final String imAccId) {
        showLoadingView();
        IMHelper.getInstance(mContext).refreshTokenAndLogin(new IMHelper.OnRefreshTokenListener() {
            @Override
            public void onRefreshComplete(boolean success) {
                dismissLoadingView();
                if (success) {
                    NavigationHelper.toSessionDetail(SendVerifyRequestActivity.this, imAccId);
                } else {
                    toastMessage("IM初始化失败");
                }
                closePageSucc();
            }
        });
    }

    private void closePageSucc() {
        setResult(RESULT_OK);
        finish();
    }

}