package com.hm.iou.msg.api;

import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.msg.bean.FriendApplyRecordListBean;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendListBean;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.msg.bean.ReportItemBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.AddFriendReqBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
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
        return getService().getUnReadMsgNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取管家消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<HmMsgBean>>> getHmMsgList() {
        return getService().getHmMsgList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取合同消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<ContractMsgDbData>>> getContractMsgList() {
        return getService().getContractMsgList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * 获取待还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<RemindBackMsgDbData>>> getRemindBackList() {
        return getService().getRemindBackList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获疑似合同还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<SimilarityContractMsgDbData>>> getSimilarityContractList() {
        return getService().getSimilarityContractList()
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

    public static Flowable<BaseResponse<FriendInfo>> getUserInfoById(String userId) {
        return getService().getUserInfoById(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<BaseResponse<Object>> addFriendRequest(String userId, String applyMsg) {
        AddFriendReqBean data = new AddFriendReqBean();
        data.setFriendId(userId);
        data.setApplyMsg(applyMsg);
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

}