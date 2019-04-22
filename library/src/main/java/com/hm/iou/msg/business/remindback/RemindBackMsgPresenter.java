package com.hm.iou.msg.business.remindback;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.remindback.view.IRemindBackMsgItem;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
        getMsgList(false);
    }

    @Override
    public void getMsgList(final boolean isShowTip) {
        MsgApi.getRemindBackList()
                .compose(getProvider().<BaseResponse<List<RemindBackMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<RemindBackMsgDbData>>handleResponse())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<RemindBackMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<RemindBackMsgDbData> list) {
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        getCache(list);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        getCache(null);
                    }

                    @Override
                    public boolean isShowCommError() {
                        return isShowTip;
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return isShowTip;
                    }
                });
    }

    private void getCache(final List<RemindBackMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<IRemindBackMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IRemindBackMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateRemindBackMsgList(list);
                List<RemindBackMsgDbData> listCache = MsgCenterDbHelper.getRemindBackMsgList();
                List<IRemindBackMsgItem> resultList = DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IRemindBackMsgItem>>() {
                    @Override
                    public void accept(List<IRemindBackMsgItem> resultList) throws Exception {
                        mView.hideInitLoading();
                        mView.hidePullDownRefresh();
                        mView.enableRefresh();
                        if (resultList == null || resultList.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(resultList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideInitLoading();
                        mView.hidePullDownRefresh();
                        mView.enableRefresh();
                        mView.showDataEmpty();
                    }
                });
    }


}
