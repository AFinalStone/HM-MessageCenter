package com.hm.iou.msg.business.friend.view;

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
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.StringUtil;
import com.hm.iou.tools.ToastUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by hjy on 2019/4/12.
 */

public class SendVerifyRequestActivity extends BaseActivity {

    public static final String EXTRA_KEY_USER_ID = "user_id";

    @BindView(R2.id.et_verify_content)
    EditText mEtContent;
    @BindView(R2.id.btn_verify_send)
    Button mBtnSend;

    private String mUserId;

    private Disposable mDisposable;

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
        mUserId = data.getStringExtra(EXTRA_KEY_USER_ID);
        if (bundle != null) {
            mUserId = bundle.getString(EXTRA_KEY_USER_ID);
        }
        RxTextView.textChanges(mEtContent).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence sequence) throws Exception {
                mBtnSend.setEnabled(TextUtils.isEmpty(sequence) ? false : true);
            }
        });

        UserInfo userInfo = UserManager.getInstance(this).getUserInfo();
        mEtContent.setText("我是" + StringUtil.getUnnullString(userInfo.getNickName()));
        mEtContent.setSelection(mEtContent.length());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_USER_ID, mUserId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @OnClick(value = {R2.id.btn_verify_send})
    void onClick(View v) {
        if (v.getId() == R.id.btn_verify_send) {
            String content = mEtContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                return;
            }
            sendVerifyRequest(content);
        }
    }

    private void sendVerifyRequest(String content) {
        showLoadingView();
        mDisposable = MsgApi.addFriendRequest(mUserId, content)
                .map(RxUtil.handleResponse())
                .subscribeWith(new CommSubscriber<Object>(this) {
                    @Override
                    public void handleResult(Object o) {
                        dismissLoadingView();
                        ToastUtil.showStatusView(SendVerifyRequestActivity.this, "申请已发送");
                        //获取token并登陆IM
                        IMHelper.getInstance(mContext).refreshTokenAndLogin();
                        closeCurrPage();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        dismissLoadingView();
                    }
                });
    }

}