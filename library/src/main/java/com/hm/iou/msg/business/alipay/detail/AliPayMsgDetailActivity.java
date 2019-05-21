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
import com.hm.iou.msg.business.alipay.pdf.ALiPayPdfActivity;
import com.hm.iou.router.Router;
import com.hm.iou.uikit.HMBottomBarView;
import com.hm.iou.uikit.HMLoadingView;

import butterknife.BindView;

public class AliPayMsgDetailActivity extends BaseActivity<AliPayMsgDetailPresenter> implements AliPayMsgDetailContract.View {


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
        mPresenter.getDetail();
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
                mPresenter.getDetail();
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
    public void showSeeBtn(final String pdfUrl) {
        mBottomBar.setTitleVisible(true);
        mBottomBar.updateTitle("查看");
        mBottomBar.setTitleBackgournd(R.drawable.uikit_selector_btn_bordered_minor_small);
        mBottomBar.setOnTitleClickListener(new HMBottomBarView.OnTitleClickListener() {
            @Override
            public void onClickTitle() {
                Logger.d("查看的Pdf链接为====" + pdfUrl);
                Intent intent = new Intent(mContext, ALiPayPdfActivity.class);
                intent.putExtra(ALiPayPdfActivity.EXTRA_KEY_PDF_URL, "https://www.baidu.com");
                startActivity(intent);
            }
        });
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


}
