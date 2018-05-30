package com.hm.iou.msg.business.message;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.msg.business.message.view.IMsgItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by syl on 18/4/28.<br>
 */

public class MsgCenterPresenter extends MvpActivityPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {


    List<IMsgItem> mMsgsList;

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
    }


    @Override
    public void getMsgList() {
        Flowable.just(1)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<Integer>(mView) {
                    @Override
                    public void handleResult(Integer integer) {
                        if (mMsgsList == null) {
                            mMsgsList = new ArrayList<>();
                        }
                        mMsgsList.add(new MsgDetailBean());
                        mMsgsList.add(new MsgDetailBean());
                        mView.showMsgList(mMsgsList);
                        mView.hidePullDownRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }
}
