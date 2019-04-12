package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.ApplyApplyNewFriendBean;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.bean.FriendBean;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.msg.bean.SimilarityContractMsgBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.bean.req.GetHmMsgListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarityContractListReq;
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
    public static Flowable<BaseResponse<List<HmMsgBean>>> getHmMsgList(GetHmMsgListReq req) {
        return getService().getHmMsgList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取合同消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<ContractMsgBean>>> getContractMsgList(GetContractMsgListReq req) {
        return getService().getContractMsgList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取待还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<RemindBackMsgBean>>> getRemindBackList(GetRemindBackListReq req) {
        return getService().getRemindBackList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获疑似合同还消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<SimilarityContractMsgBean>>> getSimilarityContractList(GetSimilarityContractListReq req) {
        return getService().getSimilarityContractList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通讯录
     *
     * @return
     */
    public static Flowable<BaseResponse<List<FriendBean>>> getFriendList(GetFriendListReq req) {
        return getService().getFriendList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通讯录
     *
     * @return
     */
    public static Flowable<BaseResponse<List<ApplyApplyNewFriendBean>>> getApplyNewFriendList(GetApplyNewFriendListReq req) {
        return getService().getApplyNewFriendList(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}