package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.AddFeedbackReqBean;
import com.hm.iou.msg.bean.FeedbackDetailBean;
import com.hm.iou.msg.bean.FeedbackListItemBean;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hjy on 18/4/27.<br>
 */

public interface MsgService {

    @POST("/api/iou/user/v1/selectComplainsByCustomerId")
    Flowable<BaseResponse<List<FeedbackListItemBean>>> getHistoryFeedbackList();

    @POST("/api/iou/user/v1/selectComplainById")
    Flowable<BaseResponse<FeedbackDetailBean>> getFeedbackDetail(@Query("autoId") String recordId);

    @POST("/api/iou/user/v1/addComplain")
    Flowable<BaseResponse<Object>> sendFeedback(@Body AddFeedbackReqBean reqBean);

    @GET("/api/message/messageCenter/v1/getMessages")
    Flowable<BaseResponse<List<MsgDetailBean>>> getMessages();

}