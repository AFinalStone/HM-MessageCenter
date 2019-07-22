package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.CheckForIMChatResBean;
import com.hm.iou.msg.bean.FriendApplyRecordListBean;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendListBean;
import com.hm.iou.msg.bean.FriendRelationResBean;
import com.hm.iou.msg.bean.GetAliPayListMsgResBean;
import com.hm.iou.msg.bean.GetAliPayMsgDetailResBean;
import com.hm.iou.msg.bean.GetContractMsgListResBean;
import com.hm.iou.msg.bean.GetHMMsgListResBean;
import com.hm.iou.msg.bean.GetOrRefreshIMTokenBean;
import com.hm.iou.msg.bean.GetRemindBackListMsgResBean;
import com.hm.iou.msg.bean.GetSimilarityContractListResBean;
import com.hm.iou.msg.bean.ReportItemBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.AddFriendReqBean;
import com.hm.iou.msg.bean.req.AgreeFriendReqBean;
import com.hm.iou.msg.bean.req.FriendDetailReqBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgDetailReqBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgListReq;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.bean.req.GetHMMsgListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarContractMessageReqBean;
import com.hm.iou.msg.bean.req.MakeMsgHaveReadReqBean;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.bean.req.ReportUserReqBean;
import com.hm.iou.msg.bean.req.UpdateApplyRemarkReqBean;
import com.hm.iou.msg.bean.req.UpdateRemarkNameReqBean;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author syl
 * @time 2019/5/22 10:15 AM
 */
public interface MsgService {

    @GET("/api/message/messageCenter/v2/unReadMessages")
    Flowable<BaseResponse<UnReadMsgNumBean>> getUnReadMsgNum(@Query("tab") int tab);

    @POST("/api/message/messageCenter/v2/getButlerMsgCache")
    Flowable<BaseResponse<GetHMMsgListResBean>> getHmMsgList(@Body GetHMMsgListReq req);

    @POST("/api/message/messageCenter/v2/getContractMsgCache")
    Flowable<BaseResponse<GetContractMsgListResBean>> getContractMsgList(@Body GetContractMsgListReq req);

    @POST("/api/message/messageCenter/v2/getWaitRepayMsgCache")
    Flowable<BaseResponse<GetRemindBackListMsgResBean>> getRemindBackList(@Body GetRemindBackListReq req);

    @POST("/api/message/messageCenter/v2/getSimilarContractMsgCache")
    Flowable<BaseResponse<GetSimilarityContractListResBean>> getSimilarityContractList(@Body GetSimilarContractMessageReqBean req);

    @POST("/api/message/messageCenter/v2/readSingle")
    Flowable<BaseResponse<Object>> makeMsgHaveRead(@Body MakeMsgHaveReadReqBean req);

    @POST("/api/message/messageCenter/v2/readBatch")
    Flowable<BaseResponse<Integer>> makeMsgTypeAllHaveRead(@Body MakeMsgTypeAllHaveReadReqBean req);

    @POST("/api/message/messageCenter/v2/getReceiptMsgCache")
    Flowable<BaseResponse<GetAliPayListMsgResBean>> getAliPayMsgList(@Body GetAliPayMsgListReq req);

    @POST("/api/message/messageCenter/v2/getAlipayReceiptInfo")
    Flowable<BaseResponse<GetAliPayMsgDetailResBean>> getAliPayMsgDetail(@Body GetAliPayMsgDetailReqBean req);

    @POST("/api/news/friend/v1/getMailList")
    Flowable<BaseResponse<FriendListBean>> getFriendList(@Body GetFriendListReq req);

    @POST("/api/news/friend/v2/getApplyRecordList")
    Flowable<BaseResponse<FriendApplyRecordListBean>> getApplyNewFriendList(@Body GetApplyNewFriendListReq req);

    @GET("/api/iou/user/v1/getCustomerFeedback")
    Flowable<BaseResponse<List<ReportItemBean>>> getReportList(@Query("scene") int scene);

    @POST("/api/news/friend/v1/addReportById")
    Flowable<BaseResponse<Object>> reportUser(@Body ReportUserReqBean data);

    @POST("/api/news/friend/v2/getUserInfoById")
    Flowable<BaseResponse<FriendInfo>> getUserInfoById(@Body FriendDetailReqBean data);

    @POST("/api/news/friend/v2/addFriends")
    Flowable<BaseResponse<Boolean>> addFriendRequest(@Body AddFriendReqBean data);

    @POST("/api/news/friend/v1/updateStageName")
    Flowable<BaseResponse<Object>> updateRemarkName(@Body UpdateRemarkNameReqBean data);

    @POST("/api/news/friend/v1/updateApplyMsg")
    Flowable<BaseResponse<Object>> updateApplyRemarkName(@Body UpdateApplyRemarkReqBean data);


    @GET("/api/news/friend/v1/addBlackById")
    Flowable<BaseResponse<Object>> addBlackName(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/addBlackAndRemoveFriend")
    Flowable<BaseResponse<Object>> addBlackAndRemoveFriend(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/removeBlackById")
    Flowable<BaseResponse<Object>> removeBlackName(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/removeFriendById")
    Flowable<BaseResponse<Object>> removeFriendById(@Query("friendId") String friendId);

    @GET("/api/iou/front/v2/countSameIOU")
    Flowable<BaseResponse<Integer>> countSameIOU(@Query("friendId") String friendId);

    @POST("/api/news/friend/v2/agreeApply")
    Flowable<BaseResponse<Object>> agreeApply(@Body AgreeFriendReqBean reqBean);

    @GET("/api/news/friend/v1/delApplyRecord")
    Flowable<BaseResponse<Object>> deleteApplyRecord(@Query("applyId") String applyId);

    @GET("api/news/friend/v1/refuseAndDelApplyRecord")
    Flowable<BaseResponse<Object>> refuseAndDelApplyRecord(@Query("applyId") String applyId);

    @GET("/api/news/friend/v1/getOrRefreshToken")
    Flowable<BaseResponse<GetOrRefreshIMTokenBean>> getOrRefreshIMToken();

    @POST("/api/iou/front/v2/moneyV2/includeBatch")
    Flowable<BaseResponse<Integer>> includeBatch(@Body List<String> list);

    @GET("/api/news/friend/v1/friendRelation")
    Flowable<BaseResponse<FriendRelationResBean>> findFriendRelation(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/refreshFriendApply")
    Flowable<BaseResponse<String>> refreshFriendApply(@Query("friendId") String friendId);

    @GET("/api/news/friend/v1/checkForIMChat")
    Flowable<BaseResponse<CheckForIMChatResBean>> checkForIMChat(@Query("friendId") String friendId);

}