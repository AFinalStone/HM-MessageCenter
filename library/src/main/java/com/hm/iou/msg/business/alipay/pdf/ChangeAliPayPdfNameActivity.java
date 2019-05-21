package com.hm.iou.msg.business.alipay.pdf;

import android.os.Bundle;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.msg.R;

public class ChangeAliPayPdfNameActivity<T extends MvpActivityPresenter> extends BaseActivity<T> {


    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_alipay_msg_change_pdf_name;
    }

    @Override
    protected T initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle bundle) {

    }
}
