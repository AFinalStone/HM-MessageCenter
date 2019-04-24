package com.hm.iou.msg.business.similarity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetSimilarityContractListResBean;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class SimilarityContractMsgPresenter extends MvpActivityPresenter<SimilarityContractMsgContract.View> implements SimilarityContractMsgContract.Presenter {

    private static final int PAGE_SIZE = 5;//每次请求的每页大小
    private static final int PAGE_DEFAULT_FIRST = 1;//首页
    private int mCurrentPage = 0;//当前请求的页码
    private List<ISimilarityContractMsgItem> mListData = new ArrayList<>();

    public SimilarityContractMsgPresenter(@NonNull Context context, @NonNull SimilarityContractMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        mCurrentPage = PAGE_DEFAULT_FIRST;
        MsgApi.getSimilarityContractList(mCurrentPage, PAGE_SIZE)
                .compose(getProvider().<BaseResponse<GetSimilarityContractListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetSimilarityContractListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetSimilarityContractListResBean>(mView) {
                    @Override
                    public void handleResult(GetSimilarityContractListResBean resBean) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        mListData.clear();
                        if (resBean.getList() == null || resBean.getList().isEmpty()) {
                            mView.showDataEmpty();
                            return;
                        }
                        List<GetSimilarityContractListResBean.ListBean> list = resBean.getList();
                        List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(list);
                        mView.showMsgList(resultList);
                        //是否没有更多数据了
                        mListData.addAll(resultList);
                        if (mListData.size() >= resBean.getTotal()) {
                            mView.showLoadMoreEnd();
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        mView.showInitFailed(msg);
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
    public void refreshData() {
        mCurrentPage = PAGE_DEFAULT_FIRST;
        MsgApi.getSimilarityContractList(mCurrentPage, PAGE_SIZE)
                .compose(getProvider().<BaseResponse<GetSimilarityContractListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetSimilarityContractListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetSimilarityContractListResBean>(mView) {
                    @Override
                    public void handleResult(GetSimilarityContractListResBean resBean) {
                        mView.hidePullDownRefresh();
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        mListData.clear();
                        if (resBean.getList() == null || resBean.getList().isEmpty()) {
                            mView.showDataEmpty();
                            return;
                        }
                        List<GetSimilarityContractListResBean.ListBean> list = resBean.getList();
                        List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(list);
                        mView.showMsgList(resultList);
                        //是否没有更多数据了
                        mListData.addAll(resultList);
                        if (mListData.size() >= resBean.getTotal()) {
                            mView.showLoadMoreEnd();
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }

                });
    }

    @Override
    public void getMoreData() {
        mCurrentPage++;
        MsgApi.getSimilarityContractList(mCurrentPage, PAGE_SIZE)
                .compose(getProvider().<BaseResponse<GetSimilarityContractListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetSimilarityContractListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetSimilarityContractListResBean>(mView) {
                    @Override
                    public void handleResult(GetSimilarityContractListResBean resBean) {
                        mView.showLoadMoreComplete();
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        List<GetSimilarityContractListResBean.ListBean> list = resBean.getList();
                        List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(list);
                        mView.showMoreNewsList(resultList);
                        //是否没有更多数据了
                        mListData.addAll(resultList);
                        if (mListData.size() >= resBean.getTotal()) {
                            mView.showLoadMoreEnd();
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String msg) {
                        mView.showLoadMoreFail();
                        mCurrentPage--;
                    }

                });
    }

}
