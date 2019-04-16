package com.hm.iou.msg.business.message;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Publisher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class MsgCenterPresenter extends MvpFragmentPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {

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
        if (MsgCenterAppLike.getInstance().mHaveInitIM) {
            //  注册/注销观察者
            NIMClient.getService(MsgServiceObserve.class)
                    .observeRecentContact(mChatListObserver, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (MsgCenterAppLike.getInstance().mHaveInitIM) {
            //  注册/注销观察者
            NIMClient.getService(MsgServiceObserve.class)
                    .observeRecentContact(mChatListObserver, false);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void getHeaderModules() {
        mView.showInitLoading();
        MsgApi.getUnReadMsgNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<BaseResponse<UnReadMsgNumBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .map(RxUtil.<UnReadMsgNumBean>handleResponse())
                .flatMap(new Function<UnReadMsgNumBean, Publisher<List<MsgListHeaderBean>>>() {
                    @Override
                    public Flowable<List<MsgListHeaderBean>> apply(UnReadMsgNumBean unReadMsgNumBean) throws Exception {
                        final int contractMsgNum = unReadMsgNumBean.getContractNumber();
                        final int similarMsgNum = unReadMsgNumBean.getSimilarContractNumber();
                        final int hmMsgNum = unReadMsgNumBean.getButlerMessageNumber();
                        final int remindBackNum = unReadMsgNumBean.getWaitRepayNumber();
                        return Flowable.create(new FlowableOnSubscribe<List<MsgListHeaderBean>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<MsgListHeaderBean>> e) throws Exception {
                                List<MsgListHeaderBean> headerModels = readDataFromAssert();
                                if (headerModels != null) {
                                     for (MsgListHeaderBean bean : headerModels) {
                                        if (ModuleType.CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                                            bean.setRedMsgNum(contractMsgNum);
                                            continue;
                                        }
                                        if (ModuleType.SIMILARITY_CONTRACT_MSG.getTypeId().equals(bean.getModuleId())) {
                                            bean.setRedMsgNum(similarMsgNum);
                                            continue;
                                        }
                                        if (ModuleType.HM_MSG.getTypeId().equals(bean.getModuleId())) {
                                            bean.setRedMsgNum(hmMsgNum);
                                            continue;
                                        }
                                        if (ModuleType.REMIND_BACK_MSG.getTypeId().equals(bean.getModuleId())) {
                                            bean.setRedMsgNum(remindBackNum);
                                            continue;
                                        }
                                    }
                                }
                                e.onNext(headerModels);
                            }
                        }, BackpressureStrategy.ERROR);
                    }
                })
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

    @Override
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
//                        for (ChatMsgBean model : newList) {
//                            int index = mChatList.indexOf(model);
//                            if (index == -1) {
//                                mChatList.add(model);
//                            } else {
//                                mChatList.set(index, model);
//                            }
//                        }
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

    @Override
    public void getUnReadMsgNum() {

    }

    @Override
    public void markHaveRead(int position) {
    }

    @Override
    public void getRedFlagCount() {
        mRedFlagCount = MsgCenterAppLike.getInstance().getTopHeadRedFlagCount();
        mView.updateRedFlagCount(mRedFlagCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            mRedFlagCount = commBizEvent.content;
            mView.updateRedFlagCount(mRedFlagCount);
        }
    }

}
