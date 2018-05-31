package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.AddFeedbackReqBean;
import com.hm.iou.msg.bean.FeedbackDetailBean;
import com.hm.iou.msg.bean.FeedbackListItemBean;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjy on 18/5/3.<br>
 */

public class MsgApi {

    private static MsgService getService() {
        return HttpReqManager.getInstance().getService(MsgService.class);
    }

    public static Flowable<BaseResponse<List<FeedbackListItemBean>>> getFeedbackList() {
        return getService().getHistoryFeedbackList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<FeedbackDetailBean>> getFeedbackDetail(String recordId) {
        return getService().getFeedbackDetail(recordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Flowable<BaseResponse<Object>> sendFeedback(int kind, String content, String pics, String userId) {
        AddFeedbackReqBean reqBean = new AddFeedbackReqBean();
        reqBean.setKind(kind);
        reqBean.setContent(content);
        reqBean.setPics(pics);
        reqBean.setCustomerId(userId);
        return getService().sendFeedback(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取消息中心的消息，每次只获取最新的消息，获取过的不会再给
     *
     * @return
     */
    public static Flowable<BaseResponse<List<MsgDetailBean>>> getMessages() {
        return getService().getMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}