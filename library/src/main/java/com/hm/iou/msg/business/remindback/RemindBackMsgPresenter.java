package com.hm.iou.msg.business.remindback;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgDbHelper;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 待还提醒
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class RemindBackMsgPresenter extends MvpActivityPresenter<RemindBackMsgContract.View> implements RemindBackMsgContract.Presenter {


    public RemindBackMsgPresenter(@NonNull Context context, @NonNull RemindBackMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        MsgApi.getRemindBackList()
                .compose(getProvider().<BaseResponse<List<RemindBackMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<RemindBackMsgDbData>>handleResponse())
                .map(new Function<List<RemindBackMsgDbData>, List<RemindBackMsgDbData>>() {
                    @Override
                    public List<RemindBackMsgDbData> apply(List<RemindBackMsgDbData> list) throws Exception {
                        MsgDbHelper.saveOrUpdateRemindBackMsgList(list);
                        List<RemindBackMsgDbData> resultList = MsgDbHelper.getRemindBackMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<RemindBackMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<RemindBackMsgDbData> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(list));
                        }
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
    public void getMsgList() {
        MsgApi.getRemindBackList()
                .compose(getProvider().<BaseResponse<List<RemindBackMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<RemindBackMsgDbData>>handleResponse())
                .map(new Function<List<RemindBackMsgDbData>, List<RemindBackMsgDbData>>() {
                    @Override
                    public List<RemindBackMsgDbData> apply(List<RemindBackMsgDbData> list) throws Exception {
                        MsgDbHelper.saveOrUpdateRemindBackMsgList(list);
                        List<RemindBackMsgDbData> resultList = MsgDbHelper.getRemindBackMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<RemindBackMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<RemindBackMsgDbData> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(list));
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
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


}
