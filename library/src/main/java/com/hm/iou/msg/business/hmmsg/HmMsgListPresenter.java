package com.hm.iou.msg.business.hmmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.HmMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.hmmsg.view.IHmMsgItem;
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
        //重新获取未读消息数量
        MsgApi.getHmMsgList()
                .compose(getProvider().<BaseResponse<List<HmMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<HmMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<HmMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgDbData> list) {
                        getCache(list, true);
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        getCache(null, true);
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }

                    @Override
                    public boolean isShowBusinessError() {
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
                .subscribeWith(new CommSubscriber<List<HmMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgDbData> list) {
                        getCache(list, false);
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        getCache(null, false);
                    }
                });
    }


    private void getCache(final List<HmMsgDbData> list, final boolean isInit) {
        Flowable.create(new FlowableOnSubscribe<List<IHmMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IHmMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateHmMsgList(list);
                List<HmMsgDbData> listCache = MsgCenterDbHelper.getHmMsgList();
                List<IHmMsgItem> resultList = DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IHmMsgItem>>() {
                    @Override
                    public void accept(List<IHmMsgItem> resultList) throws Exception {
                        //关闭动画
                        if (isInit) {
                            mView.hideInitLoading();
                            mView.enableRefresh();
                            mView.scrollToBottom();
                        } else {
                            mView.hidePullDownRefresh();
                        }
                        if (resultList == null || resultList.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(resultList);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //关闭动画
                        if (isInit) {
                            mView.hideInitLoading();
                            mView.enableRefresh();
                            mView.scrollToBottom();
                        } else {
                            mView.hidePullDownRefresh();
                        }
                        mView.showDataEmpty();
                    }
                });
    }


}
