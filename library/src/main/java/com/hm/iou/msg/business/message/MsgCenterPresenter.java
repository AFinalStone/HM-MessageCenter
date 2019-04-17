package com.hm.iou.msg.business.message;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.im.IMInitHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class MsgCenterPresenter extends MvpFragmentPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {

    private List<MsgListHeaderBean> mHeaderList;
    private List<ChatMsgBean> mChatList;
    private String mRedFlagCount;
    //  创建观察者对象
    Observer<List<RecentContact>> mChatListObserver;

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
        mChatList = new ArrayList<>();
        if (mChatListObserver == null) {
            mChatListObserver = new Observer<List<RecentContact>>() {
                @Override
                public void onEvent(List<RecentContact> messages) {
                    if (mView != null) {
                        List<ChatMsgBean> newList = DataChangeUtil.changeRecentContactToIChatMsgItem(messages);
                        for (ChatMsgBean model : newList) {
                            int index = mChatList.indexOf(model);
                            if (index == -1) {
                                mChatList.add(0, model);
                            } else {
                                mChatList.set(index, model);
                            }
                        }
                        mView.showMsgList(mChatList);
                    }
                }
            };
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        EventBus.getDefault().register(this);
        if (IMInitHelper.mHaveInitIM) {
            //  注册/注销观察者
            NIMClient.getService(MsgServiceObserve.class)
                    .observeRecentContact(mChatListObserver, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (IMInitHelper.mHaveInitIM) {
            //  注册/注销观察者
            NIMClient.getService(MsgServiceObserve.class)
                    .observeRecentContact(mChatListObserver, false);
        }
    }

    @Override
    public void init() {
        getHeaderModules();
        getChatList();
        getRedFlagCount();
        MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void refreshData() {
        getChatList();
        MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
    }


    private void getHeaderModules() {
        mView.showInitLoading();
        Flowable.create(new FlowableOnSubscribe<List<MsgListHeaderBean>>() {
            @Override
            public void subscribe(FlowableEmitter<List<MsgListHeaderBean>> e) throws Exception {
                mHeaderList = readDataFromAssert();
                UnReadMsgNumBean unReadMsgNumBean = CacheDataUtil.getNoReadMsgNum(mContext);
                if (unReadMsgNumBean != null && mHeaderList != null) {
                    for (MsgListHeaderBean bean : mHeaderList) {
                        if (ModuleType.CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                            bean.setRedMsgNum(unReadMsgNumBean.getContractNumber());
                            continue;
                        }
                        if (ModuleType.SIMILARITY_CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                            bean.setRedMsgNum(unReadMsgNumBean.getSimilarContractNumber());
                            continue;
                        }
                        if (ModuleType.HM_MSG.getTypeId().equals(bean.getModuleId())) {
                            bean.setRedMsgNum(unReadMsgNumBean.getButlerMessageNumber());
                            continue;
                        }
                        if (ModuleType.REMIND_BACK_MSG.getTypeId().equals(bean.getModuleId())) {
                            bean.setRedMsgNum(unReadMsgNumBean.getWaitRepayNumber());
                            continue;
                        }
                        if (ModuleType.NEW_APPLY_FRIEND.getTypeId().equals(bean.getModuleId())) {
                            bean.setRedMsgNum(unReadMsgNumBean.getFriendMessageNumber());
                            continue;
                        }
                    }
                }
                e.onNext(mHeaderList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<MsgListHeaderBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeWith(new CommSubscriber<List<MsgListHeaderBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgListHeaderBean> msgListHeaderBeans) {
                        mView.hideInitLoading();
                        mView.showHeaderModule(msgListHeaderBeans);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hideInitLoading();
                    }
                });
    }

    /**
     * 从assert文件中读取数据
     *
     * @return
     */
    private List<MsgListHeaderBean> readDataFromAssert() {
        AssetManager manager = mContext.getAssets();
        try {
            InputStream inputStream = manager.open("msgcenter_msg_list_header_module_data.json");
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer);
            Gson gson = new Gson();
            List<MsgListHeaderBean> list = gson.fromJson(json, new TypeToken<List<MsgListHeaderBean>>() {
            }.getType());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取会话列表
     */
    public void getChatList() {
        Flowable.create(new FlowableOnSubscribe<List<RecentContact>>() {
            @Override
            public void subscribe(FlowableEmitter<List<RecentContact>> e) throws Exception {
                List<RecentContact> recentChatList = NIMClient.getService(MsgService.class).queryRecentContactsBlock();
                e.onNext(recentChatList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<RecentContact>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeWith(new CommSubscriber<List<RecentContact>>(mView) {
                    @Override
                    public void handleResult(List<RecentContact> recentContacts) {
                        List<ChatMsgBean> newList = DataChangeUtil.changeRecentContactToIChatMsgItem(recentContacts);
                        mChatList.clear();
                        mChatList.addAll(newList);
                        mView.showMsgList(mChatList);
                        mView.hidePullDownRefresh();
                        mView.enableRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                        mView.enableRefresh();
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

    /**
     * 获取左侧头部红点数量
     */
    public void getRedFlagCount() {
        mRedFlagCount = MsgCenterMsgUtil.getTopHeadRedFlagCount();
        mView.updateRedFlagCount(mRedFlagCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            mRedFlagCount = commBizEvent.content;
            mView.updateRedFlagCount(mRedFlagCount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenGetNoReadNumSuccess(CommBizEvent commBizEvent) {
        if (MsgCenterAppLike.EXTRA_KEY_GET_NO_READ_NUM_SUCCESS.equals(commBizEvent.key)) {
            UnReadMsgNumBean unReadMsgNumBean = CacheDataUtil.getNoReadMsgNum(mContext);
            if (unReadMsgNumBean != null && mHeaderList != null) {
                for (MsgListHeaderBean bean : mHeaderList) {
                    if (ModuleType.CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getContractNumber());
                        if (mView != null) {
                            mView.refreshHeaderModule(bean);
                        }
                        continue;
                    }
                    if (ModuleType.SIMILARITY_CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getSimilarContractNumber());
                        if (mView != null) {
                            mView.refreshHeaderModule(bean);
                        }
                        continue;
                    }
                    if (ModuleType.HM_MSG.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getButlerMessageNumber());
                        if (mView != null) {
                            mView.refreshHeaderModule(bean);
                        }
                        continue;
                    }
                    if (ModuleType.REMIND_BACK_MSG.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getWaitRepayNumber());
                        if (mView != null) {
                            mView.refreshHeaderModule(bean);
                        }
                        continue;
                    }
                    if (ModuleType.NEW_APPLY_FRIEND.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getFriendMessageNumber());
                        if (mView != null) {
                            mView.refreshHeaderModule(bean);
                        }
                        continue;
                    }
                }
            }
        }
    }

}
