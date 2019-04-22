package com.hm.iou.msg.business.similarity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
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
public class SimilarityContractMsgPresenter extends MvpActivityPresenter<SimilarityContractMsgContract.View> implements SimilarityContractMsgContract.Presenter {


    public SimilarityContractMsgPresenter(@NonNull Context context, @NonNull SimilarityContractMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        getMsgList(false);
    }

    @Override
    public void getMsgList(final boolean isShowTip) {
        mView.showInitLoading();
        MsgApi.getSimilarityContractList()
                .compose(getProvider().<BaseResponse<List<SimilarityContractMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<SimilarityContractMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<SimilarityContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<SimilarityContractMsgDbData> list) {
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


    private void getCache(final List<SimilarityContractMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<ISimilarityContractMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<ISimilarityContractMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateSimilarityContractMsgList(list);
                List<SimilarityContractMsgDbData> listCache = MsgCenterDbHelper.getSimilarityContractMsgList();
                List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ISimilarityContractMsgItem>>() {
                    @Override
                    public void accept(List<ISimilarityContractMsgItem> resultList) throws Exception {
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
