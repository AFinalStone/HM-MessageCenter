package com.hm.iou.msg.business.message;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.MsgCenterDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.CacheDataUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class MsgCenterPresenter extends MvpActivityPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {

    private Disposable mListDisposable;
    private List<MsgDetailBean> mMsgListData;

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        Flowable.just(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                .map(new Function<Integer, List<MsgDetailBean>>() {
                    @Override
                    public List<MsgDetailBean> apply(Integer integer) throws Exception {
                        List<MsgDetailBean> listCache = CacheDataUtil.readMsgListFromCacheData();
                        for (MsgDetailBean bean : listCache) {
                            Logger.d("MsgCenterModule", bean.toString());
                        }
                        return listCache;
                    }
                })
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
                        mView.hideInitLoading();
                        mMsgListData = list;
                        if (mMsgListData == null) {
                            mMsgListData = new ArrayList<>();
                        }
                        if (mMsgListData.isEmpty()) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) mMsgListData);
                        }
                        //缓存加载成功，进行网络请求获取最新的数据
                        mView.showPullDownRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        mView.showInitLoadingFailed(msg);
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
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<MsgDetailBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
                        if (list != null) {
                            CacheDataUtil.addMsgListToCache(list);
                            mMsgListData.addAll(list);
                        }
                        if (mMsgListData.isEmpty()) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) mMsgListData);
                        }
                        mView.hidePullDownRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                        ToastUtil.showMessage(mContext, "网络不给力");
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
    public void markHaveRead(int position) {
        MsgDetailBean data = mMsgListData.get(position);
        data.setRead(true);
        CacheDataUtil.updateMsgItemToCache(data);
        mView.refreshItem(position);
    }
}

//    MsgDetailBean msgBean = new MsgDetailBean(1, 12312
//            , "http://p1.pstatp.com/large/pgc-image/1527661192654a57d03c488"
//            , "因为拍了一张照片，这名女子被警察逮捕了，引起全国关", "https://www.toutiao.com/a6561254954655285774/"
//            , false, "");
//    mMsgListData.add(msgBean);
//            msgBean=new MsgDetailBean(4,12312
//            ,"http://p1.pstatp.com/large/pgc-image/1527661192654a57d03c488"
//            ,"今晚系统升级","https://www.toutiao.com/a6561254954655285774/"
//            ,true,"3月4日22点-3月5日06点，进行系统升级，可能会 出现“服务器繁忙”等异常提示，特此公告。（客服 微信号：jietiaoguanjia2018） ");
//            mMsgListData.add(msgBean);
