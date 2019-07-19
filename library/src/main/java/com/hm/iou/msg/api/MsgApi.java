package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.CheckForIMChatResBean;
import com.hm.iou.msg.bean.FriendApplyRecordListBean;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendListBean;
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
import com.hm.iou.msg.bean.req.UpdateRemarkNameReqBean;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.model.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author syl
 * @time 2019/4/12 2:18 PM
 */

public class MsgApi {

    private static MsgService getService() {
        return HttpReqManager.getInstance().getService(MsgService.class);
    }

    /**
     * 获取消息中心未读消息数量
     *
     * @return
     */
    public static Flowable<BaseResponse<UnReadMsgNumBean>> getUnReadMsgNum() {
        return getService().getUnReadMsgNum(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取管家消息
     *
     * @return
     */
    public static Flowable<BaseResponse<GetHMMsgListResBean>> getHmMsgList(GetHMMsgListReq req) {
        return getService().getHmMsgList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取合同消息
     *
     * @return
     */
    public static Flowable<BaseResponse<GetContractMsgListResBean>> getContractMsgList(GetContractMsgListReq req) {
        return getService().getContractMsgList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取支付宝回单消息列表
     *
     * @return
     */
    public static Flowable<BaseResponse<GetAliPayListMsgResBean>> getAliPayMsgList(GetAliPayMsgListReq getAliPayMsgListReq) {
        return getService().getAliPayMsgList(getAliPayMsgListReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取支付宝回单详情页面
     *
     * @return
     */
    public static Flowable<BaseResponse<GetAliPayMsgDetailResBean>> getAliPayMsgDetail(GetAliPayMsgDetailReqBean reqBean) {
        return getService().getAliPayMsgDetail(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取待还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<GetRemindBackListMsgResBean>> getRemindBackList(GetRemindBackListReq req) {
        return getService().getRemindBackList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获疑似合同还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<GetSimilarityContractListResBean>> getSimilarityContractList(GetSimilarContractMessageReqBean reqBean) {
        return getService().getSimilarityContractList(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 把单个消息设置为已读
     *
     * @return
     */
    public static Flowable<BaseResponse<Object>> makeSingleMsgHaveRead(String msgId, String msgType) {
        MakeMsgHaveReadReqBean reqBean = new MakeMsgHaveReadReqBean();
        reqBean.setMsgId(msgId);
        reqBean.setType(msgType);
        return getService().makeMsgHaveRead(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 把某种类型的消息设置为已读
     *
     * @return
     */
    public static Flowable<BaseResponse<Integer>> makeTypeMsgHaveRead(MakeMsgTypeAllHaveReadReqBean reqBean) {
        return getService().makeMsgTypeAllHaveRead(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 批量收录吕约借条
     *
     * @return
     */
    public static Flowable<BaseResponse<Integer>> includeAllSimilarity(List<String> listId) {
        return getService().includeBatch(listId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通讯录
     *
     * @return
     */
    public static Flowable<BaseResponse<FriendListBean>> getFriendList(GetFriendListReq req) {
        return getService().getFriendList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通讯录
     *
     * @return
     */
    public static Flowable<BaseResponse<FriendApplyRecordListBean>> getApplyNewFriendList(GetApplyNewFriendListReq req) {
        return getService().getApplyNewFriendList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取不同意的原因列表
     *
     * @param scene 1:反馈告知原因 2：举报好友
     * @return
     */
    public static Flowable<BaseResponse<List<ReportItemBean>>> getReportList(int scene) {
        return getService().getReportList(scene).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> reportUser(ReportUserReqBean data) {
        return getService().reportUser(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取好友详情信息
     *
     * @param userId 好友id
     * @param idType 1-用户id，2-im账户
     * @return
     */
    public static Flowable<BaseResponse<FriendInfo>> getUserInfoById(String userId, int idType) {
        FriendDetailReqBean data = new FriendDetailReqBean();
        data.setFriendId(userId);
        data.setIdType(idType);
        return getService().getUserInfoById(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Boolean>> addFriendRequest(String userIdOrImId, String applyMsg, int idType) {
        AddFriendReqBean data = new AddFriendReqBean();
        data.setFriendId(userIdOrImId);
        data.setApplyMsg(applyMsg);
        data.setIdType(idType);
        return getService().addFriendRequest(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新用户的备注名
     *
     * @param userId     用户id
     * @param remarkName 用户备注名
     * @return
     */
    public static Flowable<BaseResponse<Object>> updateRemarkName(String userId, String remarkName) {
        UpdateRemarkNameReqBean data = new UpdateRemarkNameReqBean();
        data.setFriendId(userId);
        data.setStageName(remarkName);
        return getService().updateRemarkName(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> addBlackName(String friendId) {
        return getService().addBlackName(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> removeBlackName(String friendId) {
        return getService().removeBlackName(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> removeFriendById(String friendId) {
        return getService().removeFriendById(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Integer>> countSameIOU(String friendId) {
        return getService().countSameIOU(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> agreeApply(String friendId) {
        return getService().agreeApply(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> deleteApplyRecord(String applyId) {
        return getService().deleteApplyRecord(applyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取或者更新Im_Token
     *
     * @return
     */
    public static Flowable<BaseResponse<GetOrRefreshIMTokenBean>> getOrRefreshIMToken() {
        return getService().getOrRefreshIMToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<CheckForIMChatResBean>> checkForIMChat(String friendId) {
        return getService().checkForIMChat(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}