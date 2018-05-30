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
import com.hm.iou.msg.bean.FeedbackListItemBean;
import com.hm.iou.msg.business.feedback.FeedbackDetailContract;
import com.hm.iou.msg.business.feedback.HistoryFeedbackContract;
import com.hm.iou.msg.business.feedback.view.IFeedbackListItem;
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

import io.reactivex.disposables.Disposable;

/**
 * Created by hjy on 18/4/28.<br>
 */

public class FeedbackDetailPresenter extends MvpActivityPresenter<FeedbackDetailContract.View> implements FeedbackDetailContract.Presenter {


    public FeedbackDetailPresenter(@NonNull Context context, @NonNull FeedbackDetailContract.View view) {
        super(context, view);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void getFeedbackDetail(String id) {
        mView.showLoading(true);
        mView.showQuestionLayout(false);
        mView.showAnswerLayout(false);
        mView.showNoReplyLayout(false);
        MsgApi.getFeedbackDetail(id)
                .compose(getProvider().<BaseResponse<FeedbackDetailBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FeedbackDetailBean>handleResponse())
                .subscribeWith(new CommSubscriber<FeedbackDetailBean>(mView) {
                    @Override
                    public void handleResult(FeedbackDetailBean data) {
                        showFeedbackDetail(data);
                    }

                    @Override
                    public void handleException(Throwable t, String code, String msg) {
                        mView.showQuestionLayout(false);
                        mView.showAnswerLayout(false);
                        mView.showNoReplyLayout(false);
                        mView.showLoadingError(TextUtils.isEmpty(code) ? "网络加载失败，请检查网络" : msg);
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }
                });
    }

    private void showFeedbackDetail(FeedbackDetailBean data) {
        mView.showLoading(false);
        mView.showQuestionLayout(true);

        int type = data.getKind();
        mView.showFeedbackType("#" + FeedbackKind.getDescByValue(type));

        if (data.getStatus() == 3) {
            mView.showFeedbackStatus("已处理", 0xffa3a3a3);
        } else {
            mView.showFeedbackStatus("未处理", 0xff222222);
        }

        UserInfo userInfo = UserManager.getInstance(mContext).getUserInfo();
        int defResId = R.mipmap.uikit_icon_header_unknow;
        if (userInfo.getSex() == SexEnum.MALE.getValue()) {
            defResId = R.mipmap.uikit_icon_header_man;
        } else if (userInfo.getSex() == SexEnum.FEMALE.getValue()) {
            defResId = R.mipmap.uikit_icon_header_wuman;
        }
        mView.showUserAvatar(userInfo.getAvatarUrl(), defResId);
        mView.showQuestionTime(formatTime(data.getRecordTime()));
        mView.showFeedbackQuestion(data.getContent());

        String pics = data.getPics();
        if (TextUtils.isEmpty(pics)) {
            mView.showFeedbackImageList(null);
        } else {
            String[] arr = pics.split(",");
            List<String> urlList = new ArrayList<>();
            for (String url : arr) {
                if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
                    continue;
                }
                urlList.add(url);
            }
            mView.showFeedbackImageList(urlList);
        }

        //已处理
        if (data.getStatus() == 3) {
            mView.showAnswerLayout(true);
            mView.showNoReplyLayout(false);

            mView.showAnswerTime(formatTime(data.getAnswerTime()));
            mView.showAnswer(data.getAnswer());
        } else {
            mView.showAnswerLayout(false);
            mView.showNoReplyLayout(true);
        }
    }

    private String formatTime(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(timeStr);
            sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            String formatStr = sdf.format(date);
            return formatStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
