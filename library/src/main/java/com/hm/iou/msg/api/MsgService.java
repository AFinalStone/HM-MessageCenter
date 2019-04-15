package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.ApplyApplyNewFriendBean;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.bean.FriendBean;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.msg.bean.ReportItemBean;
import com.hm.iou.msg.bean.SimilarityContractMsgBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.AddFriendReqBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.bean.req.GetHmMsgListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarityContractListReq;
import com.hm.iou.msg.bean.req.ReportUserReqBean;
import com.hm.iou.msg.bean.req.UpdateRemarkNameReqBean;
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

    @GET("/api/message/messageCenter/v1/getUnReadMsgNum")
    Flowable<BaseResponse<UnReadMsgNumBean>> getUnReadMsgNum();

    @POST("/api/message/messageCenter/v1/getHmMsgList")
    Flowable<BaseResponse<List<HmMsgBean>>> getHmMsgList(@Body GetHmMsgListReq req);

    @POST("/api/message/messageCenter/v1/getContractMsgList")
    Flowable<BaseResponse<List<ContractMsgBean>>> getContractMsgList(@Body GetContractMsgListReq req);

    @POST("/api/message/messageCenter/v1/getRemindBackList")
    Flowable<BaseResponse<List<RemindBackMsgBean>>> getRemindBackList(@Body GetRemindBackListReq req);

    @POST("/api/message/messageCenter/v1/getSimilarityContractList")
    Flowable<BaseResponse<List<SimilarityContractMsgBean>>> getSimilarityContractList(@Body GetSimilarityContractListReq req);

    @POST("/api/news/friend/v1/getMailList")
    Flowable<BaseResponse<List<FriendBean>>> getFriendList(@Body GetFriendListReq req);

    @POST("/api/news/friend/v1/getApplyRecordList")
    Flowable<BaseResponse<List<ApplyApplyNewFriendBean>>> getApplyNewFriendList(@Body GetApplyNewFriendListReq req);

    @GET("/api/iou/user/v1/getCustomerFeedback")
    Flowable<BaseResponse<List<ReportItemBean>>> getReportList(@Query("scene") int scene);

    @POST("/api/news/friend/v1/addReportById")
    Flowable<BaseResponse<Object>> reportUser(@Body ReportUserReqBean data);

    @GET("/api/news/friend/v1/getUserInfoById")
    Flowable<BaseResponse<FriendInfo>> getUserInfoById(@Query("friendId") String userId);

    @POST("/api/news/friend/v1/addFriends")
    Flowable<BaseResponse<Object>> addFriendRequest(@Body AddFriendReqBean data);

    @POST("/api/news/friend/v1/updateStageName")
    Flowable<BaseResponse<Object>> updateRemarkName(@Body UpdateRemarkNameReqBean data);

    @GET("/api/news/friend/v1/addBlackById")
    Flowable<BaseResponse<Object>> addBlackName(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/removeBlackById")
    Flowable<BaseResponse<Object>> removeBlackName(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/removeFriendById")
    Flowable<BaseResponse<Object>> removeFriendById(@Query("friendId") String friendId);

    @GET("/api/iou/front/v2/countSameIOU")
    Flowable<BaseResponse<Integer>> countSameIOU(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/agreeApply")
    Flowable<BaseResponse<Object>> agreeApply(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/delApplyRecord")
    Flowable<BaseResponse<Object>> deleteApplyRecord(@Query("applyId") String applyId);

}