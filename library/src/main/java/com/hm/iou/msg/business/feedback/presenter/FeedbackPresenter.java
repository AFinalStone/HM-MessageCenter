package com.hm.iou.msg.business.feedback.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.R;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FeedbackDetailBean;
import com.hm.iou.msg.business.feedback.FeedbackContract;
import com.hm.iou.msg.business.feedback.FeedbackDetailContract;
import com.hm.iou.msg.dict.FeedbackKind;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.SexEnum;
import com.hm.iou.sharedata.model.UserInfo;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hjy on 18/4/28.<br>
 */

public class FeedbackPresenter extends MvpActivityPresenter<FeedbackContract.View> implements FeedbackContract.Presenter {


    public FeedbackPresenter(@NonNull Context context, @NonNull FeedbackContract.View view) {
        super(context, view);
    }

    @Override
    public void onDestroy() {

    }

}