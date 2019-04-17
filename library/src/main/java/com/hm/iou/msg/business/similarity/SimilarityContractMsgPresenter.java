package com.hm.iou.msg.business.similarity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
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
        MsgApi.getSimilarityContractList()
                .compose(getProvider().<BaseResponse<List<SimilarityContractMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<SimilarityContractMsgDbData>>handleResponse())
                .map(new Function<List<SimilarityContractMsgDbData>, List<SimilarityContractMsgDbData>>() {
                    @Override
                    public List<SimilarityContractMsgDbData> apply(List<SimilarityContractMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateSimilarityContractMsgList(list);
                        List<SimilarityContractMsgDbData> resultList = MsgCenterDbHelper.getSimilarityContractMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<SimilarityContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<SimilarityContractMsgDbData> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(list));
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
        MsgApi.getSimilarityContractList()
                .compose(getProvider().<BaseResponse<List<SimilarityContractMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<SimilarityContractMsgDbData>>handleResponse())
                .map(new Function<List<SimilarityContractMsgDbData>, List<SimilarityContractMsgDbData>>() {
                    @Override
                    public List<SimilarityContractMsgDbData> apply(List<SimilarityContractMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateSimilarityContractMsgList(list);
                        List<SimilarityContractMsgDbData> resultList = MsgCenterDbHelper.getSimilarityContractMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<SimilarityContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<SimilarityContractMsgDbData> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(list));
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }

                });
    }


}
