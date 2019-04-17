package com.hm.iou.msg.business.hmmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.HmMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class HmMsgListPresenter extends MvpActivityPresenter<HmMsgListContract.View> implements HmMsgListContract.Presenter {

    private List<HmMsgDbData> mMsgListData;

    public HmMsgListPresenter(@NonNull Context context, @NonNull HmMsgListContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        MsgApi.getHmMsgList()
                .compose(getProvider().<BaseResponse<List<HmMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<HmMsgDbData>>handleResponse())
                .map(new Function<List<HmMsgDbData>, List<HmMsgDbData>>() {
                    @Override
                    public List<HmMsgDbData> apply(List<HmMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateHmMsgList(list);
                        List<HmMsgDbData> resultList = MsgCenterDbHelper.getHmMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<HmMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgDbData> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(list));
                        }
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed();
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }
                });

    }

    @Override
    public void getMsgListFromServer() {
        //重新获取未读消息数量
        MsgApi.getHmMsgList()
                .compose(getProvider().<BaseResponse<List<HmMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<HmMsgDbData>>handleResponse())
                .map(new Function<List<HmMsgDbData>, List<HmMsgDbData>>() {
                    @Override
                    public List<HmMsgDbData> apply(List<HmMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateHmMsgList(list);
                        List<HmMsgDbData> resultList = MsgCenterDbHelper.getHmMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<HmMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgDbData> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(list));
                        }
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }

                });
    }


}
