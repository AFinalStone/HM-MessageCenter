package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.FeedbackDetailBean;
import com.hm.iou.msg.bean.FeedbackListItemBean;
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

}