package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.ApplyApplyNewFriendBean;
import com.hm.iou.msg.bean.FriendBean;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.msg.bean.SimilarityContractMsgBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarityContractListReq;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by hjy on 18/4/27.<br>
 */

public interface MsgService {

    @GET("/api/message/messageCenter/v1/getMessages")
    Flowable<BaseResponse<List<HmMsgBean>>> getMessages();

    @POST("/api/message/messageCenter/v1/getContractMsgList")
    Flowable<BaseResponse<List<ContractMsgBean>>> getContractMsgList(@Body GetContractMsgListReq req);

    @POST("/api/message/messageCenter/v1/getRemindBackList")
    Flowable<BaseResponse<List<RemindBackMsgBean>>> getRemindBackList(@Body GetRemindBackListReq req);

    @POST("/api/message/messageCenter/v1/getSimilarityContractList")
    Flowable<BaseResponse<List<SimilarityContractMsgBean>>> getSimilarityContractList(@Body GetSimilarityContractListReq req);

    @POST("/api/message/messageCenter/v1/getFriendList")
    Flowable<BaseResponse<List<FriendBean>>> getFriendList(@Body GetFriendListReq req);

    @POST("/api/message/messageCenter/v1/getApplyNewFriendList")
    Flowable<BaseResponse<List<ApplyApplyNewFriendBean>>> getApplyNewFriendList(@Body GetApplyNewFriendListReq req);

}