package com.hm.iou.msg.business.alipay.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.MsgCenterConstants;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.router.Router;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadingView;

import butterknife.BindView;

public class AliPayMsgDetailActivity extends BaseActivity<AliPayMsgDetailPresenter> implements AliPayMsgDetailContract.View {

    public static final String EXTRA_KEY_EMAIL_ID = "email_id";
    public static final String EXTRA_KEY_TYPE = "type";


    protected String mEmailId;
    protected String mType;

    @BindView(R2.id.bottomBar)
    HMBottomBarView mBottomBar;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.tv_content)
    TextView mTvContent;
    @BindView(R2.id.viewStub)
    ViewStub mViewStub;
    @BindView(R2.id.loading_init)
    HMLoadingView mLoadingInit;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_alipay_msg_detail;
    }

    @Override
    protected AliPayMsgDetailPresenter initPresenter() {
        return new AliPayMsgDetailPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        Intent intent = getIntent();
        mEmailId = intent.getStringExtra(EXTRA_KEY_EMAIL_ID);
        mType = intent.getStringExtra(EXTRA_KEY_TYPE);
        if (bundle != null) {
            mEmailId = bundle.getString(EXTRA_KEY_EMAIL_ID);
            mType = bundle.getString(EXTRA_KEY_TYPE);
        }
        try {
            int emailId = Integer.parseInt(mEmailId);
            int type = Integer.parseInt(mType);
            mPresenter.getDetail(emailId, type);
        } catch (Exception e) {
            e.printStackTrace();
            toastErrorMessage("数据异常");
            finish();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_EMAIL_ID, mEmailId);
        outState.putString(EXTRA_KEY_TYPE, mType);
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
                try {
                    int emailId = Integer.parseInt(mEmailId);
                    int type = Integer.parseInt(mType);
                    mPresenter.getDetail(emailId, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void hideInitLoading() {
        mLoadingInit.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(String title, int color) {
        mTvTitle.setText(title);
        mTvTitle.setTextColor(color);
    }

    @Override
    public void showContentText(String content) {
        mTvContent.setText(content);
    }

    @Override
    public void showHelpBtn(final String email, final String contractId) {
        mBottomBar.setTitleVisible(true);
        mBottomBar.updateTitle("帮助");
        mBottomBar.setTitleBackgournd(R.drawable.uikit_selector_btn_main_small);
        mBottomBar.setOnTitleClickListener(new HMBottomBarView.OnTitleClickListener() {
            @Override
            public void onClickTitle() {
                StringBuffer sb = new StringBuffer();
                sb.append(BaseBizAppLike.getInstance().getH5Server())
                        .append(MsgCenterConstants.H5_URL_UPLOAD_PDF_BY_MEAIL)
                        .append("?email=")
                        .append(email)
                        .append("&contractId=")
                        .append(contractId);
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                        .withString("url", sb.toString())
                        .navigation(mContext);
            }
        });
    }

    @Override
    public void showFileHaveDelete(String content) {
        View view = mViewStub.inflate();
        ((TextView) view.findViewById(R.id.tv_content)).setText(content);
        view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void showSeeBtn(final String pdfUrl, final String evidenceId, final String evidenceName) {
        mBottomBar.setTitleVisible(true);
        mBottomBar.updateTitle("查看");
        mBottomBar.setTitleBackgournd(R.drawable.uikit_selector_btn_bordered_minor_small);
        mBottomBar.setOnTitleClickListener(new HMBottomBarView.OnTitleClickListener() {
            @Override
            public void onClickTitle() {
                Logger.d("查看的Pdf链接为====" + pdfUrl);
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/iou/contract_evidence_pdf_detail")
                        .withString("pdf_url", pdfUrl)
                        .withString("evidence_id", evidenceId)
                        .withString("evidence_name", evidenceName)
                        .navigation(mContext);
            }
        });
    }


}
