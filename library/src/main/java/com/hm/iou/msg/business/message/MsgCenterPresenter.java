package com.hm.iou.msg.business.message;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.iou.base.adver.AdApi;
import com.hm.iou.base.adver.AdBean;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
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
import io.reactivex.functions.Consumer;
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
    IMHelper.OnChatListChangeListener mChatListChangeListener;
    private boolean mIsNeedRefresh = false;//是否需要刷新

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
        mChatList = new ArrayList<>();
        if (mChatListChangeListener == null) {
            mChatListChangeListener = new IMHelper.OnChatListChangeListener() {
                @Override
                public void onDataChange(List<ChatMsgBean> chatMsgBeanList) {
                    if (chatMsgBeanList == null) {
                        return;
                    }
                    for (ChatMsgBean model : chatMsgBeanList) {
                        int index = mChatList.indexOf(model);
                        if (index == -1) {
                            mChatList.add(0, model);
                        } else {
                            mChatList.set(index, model);
                        }
                    }
                    mView.showMsgList(mChatList);
                }
            };
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        EventBus.getDefault().register(this);
        IMHelper.getInstance(mContext).addOnChatListChangeListener(mChatListChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        IMHelper.getInstance(mContext).removeOnChatListChangeListener(mChatListChangeListener);
    }

    @Override
    public void init() {
        getHeaderModules();
        getChatList();
        getRedFlagCount();
        getBanner();
    }

    @Override
    public void onResume() {
        if (mIsNeedRefresh) {
            MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
            mIsNeedRefresh = false;
        }
    }

    @Override
    public void deleteItemByPosition(int position) {
        if (position >= 0 && position < mChatList.size()) {
            ChatMsgBean msgBean = mChatList.get(position);
            IMHelper.getInstance(mContext).deleteRecentContract(msgBean.getContactId());
            mChatList.remove(msgBean);
            mView.showMsgList(mChatList);
        }
    }

    @Override
    public void refreshData() {
        getChatList();
        MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
        getBanner();
    }

    /**
     * 获取顶部广告
     */
    private void getBanner() {
        AdApi.getAdvertiseList("banner003")
                .compose(getProvider().<BaseResponse<List<AdBean>>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .map(RxUtil.<List<AdBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<AdBean>>(mView) {
                    @Override
                    public void handleResult(List<AdBean> adBeans) {
                        if (adBeans != null && !adBeans.isEmpty()) {
                            mView.showTopBanner(adBeans.get(0));
                        } else {
                            mView.showTopBanner(null);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

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

    /**
     * 获取头部模块
     */
    private void getHeaderModules() {
        mView.showInitLoading();
        Flowable.create(new FlowableOnSubscribe<List<MsgListHeaderBean>>() {
            @Override
            public void subscribe(FlowableEmitter<List<MsgListHeaderBean>> e) throws Exception {
                mHeaderList = readDataFromAssert();
                e.onNext(mHeaderList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<MsgListHeaderBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<List<MsgListHeaderBean>>() {
                    @Override
                    public void accept(List<MsgListHeaderBean> msgListHeaderBeans) throws Exception {
                        mView.hideInitLoading();
                        mView.showHeaderModule(msgListHeaderBeans);
                        MsgCenterMsgUtil.getMsgCenterNoReadNumFromCache(mContext);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

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

    /**
     * 用户进入了消息列表页面，需要重新刷新页面未读消息数量
     *
     * @param commBizEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenUpdateUnReadMsgNum(UpdateMsgCenterUnReadMsgNumEvent commBizEvent) {
        mIsNeedRefresh = true;
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
