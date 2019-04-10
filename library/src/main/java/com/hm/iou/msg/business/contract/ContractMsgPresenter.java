package com.hm.iou.msg.business.contract;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class ContractMsgPresenter extends MvpActivityPresenter<ContractMsgContract.View> implements ContractMsgContract.Presenter {


    public ContractMsgPresenter(@NonNull Context context, @NonNull ContractMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void getContractList() {

    }
}
