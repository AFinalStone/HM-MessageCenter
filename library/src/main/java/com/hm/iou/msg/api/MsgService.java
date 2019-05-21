package com.hm.iou.msg.api;

import com.hm.iou.database.table.msg.AliPayMsgDbData;
import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.database.table.msg.HmMsgDbData;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.msg.bean.FriendApplyRecordListBean;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendListBean;
import com.hm.iou.msg.bean.GetAliPayMsgDetailResBean;
import com.hm.iou.msg.bean.GetOrRefreshIMTokenBean;
import com.hm.iou.msg.bean.GetSimilarityContractListResBean;
import com.hm.iou.msg.bean.ReportItemBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.AddFriendReqBean;
import com.hm.iou.msg.bean.req.FriendDetailReqBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgDetailReqBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgListReq;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.bean.req.GetSimilarContractMessageReqBean;
import com.hm.iou.msg.bean.req.MakeMsgHaveReadReqBean;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
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

    @GET("/api/message/messageCenter/v2/unReadMessages")
    Flowable<BaseResponse<UnReadMsgNumBean>> getUnReadMsgNum();

    @GET("/api/message/messageCenter/v2/getButlerMessage")
    Flowable<BaseResponse<List<HmMsgDbData>>> getHmMsgList();

    @GET("/api/message/messageCenter/v2/getContractMessage")
    Flowable<BaseResponse<List<ContractMsgDbData>>> getContractMsgList();

    @GET("/api/message/messageCenter/v2/getWaitRepayMessage")
    Flowable<BaseResponse<List<RemindBackMsgDbData>>> getRemindBackList();

    @POST("/api/message/messageCenter/v2/getSimilarContractMessage")
    Flowable<BaseResponse<GetSimilarityContractListResBean>> getSimilarityContractList(@Body GetSimilarContractMessageReqBean req);

    @POST("/api/message/messageCenter/v2/makeMsgHaveRead")
    Flowable<BaseResponse<Object>> makeMsgHaveRead(@Body MakeMsgHaveReadReqBean req);

    @POST("/api/message/messageCenter/v2/makeMsgTypeAllHaveRead")
    Flowable<BaseResponse<Object>> makeMsgTypeAllHaveRead(@Body MakeMsgTypeAllHaveReadReqBean req);

    @POST("/api/message/messageCenter/v2/getReceiptMsgCache")
    Flowable<BaseResponse<List<AliPayMsgDbData>>> getAliPayMsgList(@Body GetAliPayMsgListReq req);

    @POST("/api/message/messageCenter/v2/getAliPayReceiptInfo")
    Flowable<BaseResponse<GetAliPayMsgDetailResBean>> getAliPayMsgDetail(@Body GetAliPayMsgDetailReqBean req);

    @POST("/api/news/friend/v1/getMailList")
    Flowable<BaseResponse<FriendListBean>> getFriendList(@Body GetFriendListReq req);

    @POST("/api/news/friend/v1/getApplyRecordList")
    Flowable<BaseResponse<FriendApplyRecordListBean>> getApplyNewFriendList(@Body GetApplyNewFriendListReq req);

    @GET("/api/iou/user/v1/getCustomerFeedback")
    Flowable<BaseResponse<List<ReportItemBean>>> getReportList(@Query("scene") int scene);

    @POST("/api/news/friend/v1/addReportById")
    Flowable<BaseResponse<Object>> reportUser(@Body ReportUserReqBean data);

    @POST("/api/news/friend/v1/getUserInfoById")
    Flowable<BaseResponse<FriendInfo>> getUserInfoById(@Body FriendDetailReqBean data);

    @POST("/api/news/friend/v1/addFriends")
    Flowable<BaseResponse<Boolean>> addFriendRequest(@Body AddFriendReqBean data);

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

    @GET("/api/news/friend/v1/getOrRefreshToken")
    Flowable<BaseResponse<GetOrRefreshIMTokenBean>> getOrRefreshIMToken();

}