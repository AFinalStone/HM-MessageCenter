package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * Created by hjy on 18/4/27.<br>
 */

public interface MsgService {

    @GET("/api/message/messageCenter/v1/getMessages")
    Flowable<BaseResponse<List<HmMsgBean>>> getMessages();

}