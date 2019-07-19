package com.hm.iou.msg.business.message.presenter;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.iou.base.adver.AdApi;
import com.hm.iou.base.adver.AdBean;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.business.message.MsgCenterContract;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.event.DeleteFriendEvent;
import com.hm.iou.msg.event.UpdateFriendEvent;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.netease.nim.uikit.event.UpdateChatListEvent;
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
import io.reactivex.disposables.Disposable;
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
    private Disposable mDisposableGetChatList;//获取会话列表
    private Disposable mDisposableGetTopBanner;//获取顶部广告
    //  创建观察者对象
    IMHelper.OnChatListChangeListener mChatListChangeListener;
    private boolean mIsNeedRefreshUserInfoChatList = false;//是否需要刷新用户信息
    private boolean mIsNeedRefreshChatList = false;//是否需要刷新会话列表
    private boolean mViewIsShow = false;
    private long mLastPullFriendInfoTime;//上次拉取时间

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
                    List<String> accounts = new ArrayList<>();
                    for (ChatMsgBean model : chatMsgBeanList) {
                        int index = mChatList.indexOf(model);
                        Logger.d("model === " + model.toString());
                        if (index == -1) {
                            mChatList.add(0, model);
                            accounts.add(model.getContactId());
                        } else {
                            mChatList.set(index, model);
                        }
                    }
                    mView.showMsgList(mChatList);
                    //拉取好友列表数据
                    if (!accounts.isEmpty() && System.currentTimeMillis() - mLastPullFriendInfoTime > 60000) {
                        mLastPullFriendInfoTime = System.currentTimeMillis();
                        IMHelper.fetchUserInfoFromServer(accounts, new IMHelper.OnFetchUserInfoListener() {
                            @Override
                            public void onFetchComplete() {
                                getChatList(true);
                            }
                        });
                    }
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
        getUserInfoFromServer();
        getRedFlagCount();
        getBanner();
    }

    @Override
    public void onResume() {
        //获取未读消息
        MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
        //头像未读消息数量
        getRedFlagCount();
        //刷新好友用户信息
        if (mIsNeedRefreshUserInfoChatList) {
            getUserInfoFromServer();
            mIsNeedRefreshUserInfoChatList = false;
        }
        //刷洗会话列表
        if (mIsNeedRefreshChatList) {
            getChatList(false);
        }
    }

    @Override
    public void viewIsShow(boolean viewIsShow) {
        mViewIsShow = viewIsShow;
    }

    @Override
    public void deleteItemByPosition(int position) {
        if (position >= 0 && position < mChatList.size()) {
            ChatMsgBean msgBean = mChatList.get(position);
            IMHelper.getInstance(mContext).deleteRecentContract(msgBean.getContactId());
            mChatList.remove(msgBean);
            mView.showMsgList(mChatList);
            MsgCenterMsgUtil.getMsgCenterNoReadNumFromCache(mContext);
        }
    }

    @Override
    public void refreshData() {
        getUserInfoFromServer();
        MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
        getBanner();
    }

    /**
     * 获取顶部广告
     */
    private void getBanner() {
        if (mDisposableGetTopBanner != null && !mDisposableGetTopBanner.isDisposed()) {
            mDisposableGetTopBanner.dispose();
        }
        mDisposableGetTopBanner = AdApi.getAdvertiseList("banner005")
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
     * 从服务器获取用户资料(单次最大150)，然后获取会话列表
     */
    private void getUserInfoFromServer() {
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < 150 && i < accounts.size(); i++) {
            accounts.add(mChatList.get(i).getContactId());
        }
        if (accounts.isEmpty()) {
            getChatList(false);
            return;
        }
        IMHelper.fetchUserInfoFromServer(accounts, new IMHelper.OnFetchUserInfoListener() {
            @Override
            public void onFetchComplete() {
                getChatList(false);
            }
        });
    }

    /**
     * 获取会话列表,
     */
    public void getChatList(final boolean isSleep) {
        Logger.d("获取会话列表");
        if (mDisposableGetChatList != null && !mDisposableGetChatList.isDisposed()) {
            mDisposableGetChatList.dispose();
        }
        mDisposableGetChatList = Flowable.create(new FlowableOnSubscribe<List<ChatMsgBean>>() {
            @Override
            public void subscribe(FlowableEmitter<List<ChatMsgBean>> e) throws Exception {
                if (isSleep) {
                    SystemClock.sleep(1000);
                }
                e.onNext(IMHelper.getRecentContactList());
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<ChatMsgBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeWith(new CommSubscriber<List<ChatMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<ChatMsgBean> recentChatMsgList) {
                        for (ChatMsgBean bean : recentChatMsgList) {
                            Logger.d("bean===" + bean.toString());
                        }
                        mChatList.clear();
                        mChatList.addAll(recentChatMsgList);
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
     * 顶部头像未读消息数量
     *
     * @param commBizEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            mRedFlagCount = commBizEvent.content;
            mView.updateRedFlagCount(mRedFlagCount);
        }
    }

    /**
     * 用户修改了好友备注
     *
     * @param updateFriendEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenUpdateFriendEvent(UpdateFriendEvent updateFriendEvent) {
        mIsNeedRefreshUserInfoChatList = true;
    }

    /**
     * 同意添加好友
     *
     * @param updateFriendEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenAddFriendFriend(AddFriendEvent updateFriendEvent) {
        mIsNeedRefreshUserInfoChatList = true;
    }

    /**
     * 刷新会话列表
     *
     * @param updateChatListEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenUpdateChatList(UpdateChatListEvent updateChatListEvent) {
        if (mViewIsShow) {
            getChatList(true);
        } else {
            mIsNeedRefreshChatList = true;
        }
    }

    /**
     * 用户在通讯录页面删除了好友，及时刷新列表
     *
     * @param deleteFriendEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenDeleteFriend(DeleteFriendEvent deleteFriendEvent) {
        String account = deleteFriendEvent.imAccId;
        IMHelper.getInstance(mContext).deleteRecentContract(account);
        for (int i = 0; i < mChatList.size(); i++) {
            account.equals(mChatList.get(i).getContactId());
            mChatList.remove(i);
            break;
        }
        mView.showMsgList(mChatList);
    }

    /**
     * 成功获取消息中心未读消息数量，及时刷新UI
     *
     * @param commBizEvent
     */
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
                    if (ModuleType.ALIPAY_MSG.getTypeId().equals(bean.getModuleId())) {
                        bean.setRedMsgNum(unReadMsgNumBean.getAlipayReceiptNumber());
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
