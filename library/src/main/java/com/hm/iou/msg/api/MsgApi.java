package com.hm.iou.msg.api;

import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.msg.bean.SimilarityContractMsgBean;
import com.hm.iou.msg.bean.req.GetContractListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarityContractListReq;
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

    /**
     * 获取消息中心的消息，每次只获取最新的消息，获取过的不会再给
     *
     * @return
     */
    public static Flowable<BaseResponse<List<HmMsgBean>>> getMessages() {
        return getService().getMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取合同消息
     *
     * @return
     */
    public static Flowable<BaseResponse<List<ContractMsgBean>>> getContractList(GetContractListReq req) {
        return getService().getContractList(req)
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


}