package com.hm.iou.msg.business.contract;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.msg.business.contract.view.IContractMsgItem;

import java.util.ArrayList;
import java.util.List;

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
        List<IContractMsgItem> list = new ArrayList<>();
        IContractMsgItem item = new IContractMsgItem() {
            @Override
            public String getTitle() {
                return "合同001";
            }

            @Override
            public String getTime() {
                return "2019";
            }

            @Override
            public String getContent() {
                return "测试数据";
            }

            @Override
            public String getContractId() {
                return "合同id";
            }

            @Override
            public String getContractType() {
                return null;
            }
        };
        list.add(item);
        mView.showMsgList(list);
    }
}
