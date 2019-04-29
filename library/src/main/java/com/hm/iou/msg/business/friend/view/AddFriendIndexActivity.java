package com.hm.iou.msg.business.friend.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.comm.CommApi;
import com.hm.iou.base.comm.PowerSearchResult;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RouterUtil;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.uikit.HMTopBarView;
import com.hm.iou.uikit.dialog.HMAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by hjy on 2019/4/10.
 */

public class AddFriendIndexActivity extends BaseActivity {

    @BindView(R2.id.topBar)
    HMTopBarView mTopBarView;
    @BindView(R2.id.et_addfriend_content)
    EditText mEtContent;
    @BindView(R2.id.tv_addfriend_searchcontent)
    TextView mTvSearchContent;
    @BindView(R2.id.ll_addfriend_search)
    LinearLayout mLayoutSearch;
    @BindView(R2.id.ll_addfriend_searchcontent)
    LinearLayout mLlSearchItem;

    private Disposable mDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_add_friend_index;
    }

    @Override
    protected MvpActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mTopBarView.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
            @Override
            public void onClickTextMenu() {

            }

            @Override
            public void onClickImageMenu() {
                showAboutDialog();
            }
        });

        int h = com.hm.iou.tools.StatusBarUtil.getStatusBarHeight(this);
        mLayoutSearch.setPadding(0, h, 0, 0);

        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                mTvSearchContent.setText("搜索：" + content);
                mLlSearchItem.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            }
        });

        mEtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    doSearch();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayoutSearch.getVisibility() == View.VISIBLE) {
            mLayoutSearch.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(value = {R2.id.rl_addfriend_search, R2.id.ll_addfriend_scan, R2.id.ll_addfriend_mycard,
            R2.id.ll_addfriend_searchcontent, R2.id.tv_addfriend_cancel})
    void onClick(View v) {
        if (v.getId() == R.id.rl_addfriend_search) {
            mLayoutSearch.setVisibility(View.VISIBLE);
            mEtContent.requestFocus();
            showSoftKeyboard(mEtContent);
        } else if (v.getId() == R.id.ll_addfriend_scan) {
            NavigationHelper.toScanQrCodePage(this);
        } else if (v.getId() == R.id.ll_addfriend_mycard) {
            NavigationHelper.toMyCardPage(this);
        } else if (v.getId() == R.id.ll_addfriend_searchcontent) {
            doSearch();
        } else if (v.getId() == R.id.tv_addfriend_cancel) {
            mLayoutSearch.setVisibility(View.GONE);
            hideSoftKeyboard();
        }
    }

    private void showAboutDialog() {
        new HMAlertDialog.Builder(this)
                .setMessage("点击首页右上角扫一扫，点击我的名片，昵称下方是您的ID")
                .setPositiveButton("知道了")
                .create().show();
    }

    private void doSearch() {
        String content = mEtContent.getText().toString();
        Logger.d("search content : " + content);
        showLoadingView();
        mDisposable = CommApi.powerSearch(content, 3)
                .map(RxUtil.<PowerSearchResult>handleResponse())
                .subscribeWith(new CommSubscriber<PowerSearchResult>(this) {
                    @Override
                    public void handleResult(PowerSearchResult powerSearchResult) {
                        dismissLoadingView();
                        String url = powerSearchResult.getUrl();
                        if (!TextUtils.isEmpty(url)) {
                            RouterUtil.clickMenuLink(AddFriendIndexActivity.this, url);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        dismissLoadingView();
                    }
                });
    }

}
