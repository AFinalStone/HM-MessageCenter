package com.hm.iou.msg.business.alipay.pdf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.comm.CommApi;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.socialshare.bean.PlatFormBean;
import com.hm.iou.socialshare.business.view.SharePlatformDialog;
import com.hm.iou.socialshare.dict.PlatformEnum;
import com.hm.iou.tools.StatusBarUtil;
import com.hm.iou.uikit.dialog.HMActionSheetDialog;
import com.hm.iou.uikit.dialog.HMAlertDialog;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author syl
 * @time 2019/5/21 7:37 PM
 */

public class ALiPayPdfActivity extends BaseActivity<ALiPayPdfPresenter> implements ALiPayPdfContract.View {

    public static final String EXTRA_KEY_PDF_URL = "pdf_url";


    protected String mPdfUrl;
    @BindView(R2.id.webView_pdf)
    WebView mWebViewPdf;

    private SharePlatformDialog mShareDialog;
    private HMActionSheetDialog mMoreDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_alipay_msg_pdf_detail;
    }

    @Override
    protected ALiPayPdfPresenter initPresenter() {
        return new ALiPayPdfPresenter(this, this);
    }


    @Override
    protected void initEventAndData(Bundle bundle) {
        mPdfUrl = getIntent().getStringExtra(EXTRA_KEY_PDF_URL);
        if (bundle != null) {
            mPdfUrl = bundle.getString(EXTRA_KEY_PDF_URL);
        }
        initWebView();
        if (!TextUtils.isEmpty(mPdfUrl)) {
            Uri uri = Uri.parse(mPdfUrl);
            String path = uri.getPath();
            if (path.endsWith(".pdf")) {
                //如果是 pdf 文件地址，则用加载pdf的方式来打开
                String file = Base64.encodeToString(mPdfUrl.getBytes(), Base64.NO_WRAP);
                mWebViewPdf.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + file);
            } else {
                //如果是普通文件地址，则直接用WebView加载
                mWebViewPdf.loadUrl(mPdfUrl);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShareDialog != null) {
            mShareDialog.onDestroy();
            mShareDialog = null;
        }
    }

    protected void initWebView() {
        WebSettings settings = mWebViewPdf.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        mWebViewPdf.setWebViewClient(new WebViewClient());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mWebViewPdf.getLayoutParams();
        params.topMargin = StatusBarUtil.getStatusBarHeight(this);
        mWebViewPdf.setLayoutParams(params);

        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + ";HMAndroidWebView");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_PDF_URL, mPdfUrl);
    }

    @OnClick(value = {R2.id.ll_back, R2.id.iv_share, R2.id.iv_more})
    void onClick(View v) {
        if (v.getId() == R.id.ll_back) {
            onBackPressed();
        } else if (v.getId() == R.id.iv_share) {
            mPresenter.getShareUrl("", "");
        } else if (v.getId() == R.id.iv_more) {
            showActionSheet();
        }
    }

    public void showActionSheet() {
        if (mMoreDialog == null) {
            List<String> list = new ArrayList<>();
            list.add("重命名");
            list.add("删除");
            list.add("取消");
            mMoreDialog = new HMActionSheetDialog.Builder(this)
                    .setActionSheetList(list)
                    .setOnItemClickListener(new HMActionSheetDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(int i, String s) {
                            if (0 == i) {
                                mMoreDialog.dismiss();
                                Intent intent = new Intent(mContext, ChangeAliPayPdfNameActivity.class);
                                startActivity(intent);
                            } else if (1 == i) {
                                mMoreDialog.dismiss();
                                new HMAlertDialog.Builder(mContext)
                                        .setTitle("您确定删除吗？")
                                        .setNegativeButton("取消")
                                        .setPositiveButton("删除")
                                        .setOnClickListener(new HMAlertDialog.OnClickListener() {
                                            @Override
                                            public void onPosClick() {
                                                Logger.d("确定删除");
                                            }

                                            @Override
                                            public void onNegClick() {

                                            }
                                        }).create().show();
                            } else if (2 == i) {
                                mMoreDialog.dismiss();
                            }
                        }
                    }).create();
        }
        mMoreDialog.show();
    }


    @Override
    public void showShareDialog(String content) {
        if (mShareDialog != null) {
            mShareDialog.onDestroy();
        }
        List<PlatFormBean> list = new ArrayList<>();
        list.add(new PlatFormBean(PlatformEnum.WEIXIN));
        list.add(new PlatFormBean(PlatformEnum.QQ));
        list.add(new PlatFormBean(PlatformEnum.EMAIL));
        list.add(new PlatFormBean(PlatformEnum.SMS));
        mShareDialog = new SharePlatformDialog.Builder(this)
                .setText(content)
                .setPlatforms(list)
                .setShareListener(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        CommApi.reportShareResult(1, share_media, 1, null);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        CommApi.reportShareResult(1, share_media, 3, throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        CommApi.reportShareResult(1, share_media, 2, null);
                    }
                })
                .show();
    }


}
