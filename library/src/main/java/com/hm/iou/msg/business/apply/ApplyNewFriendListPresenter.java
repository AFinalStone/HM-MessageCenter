package com.hm.iou.msg.business.apply;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.FriendDbUtil;
import com.hm.iou.database.table.FriendApplyRecord;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendApplyRecordListBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.msg.business.apply.view.IApplyNewFriend;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.event.DeleteFriendEvent;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.scancode.CodeUtils;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.DensityUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class ApplyNewFriendListPresenter extends MvpActivityPresenter<ApplyNewFriendListContract.View> implements ApplyNewFriendListContract.Presenter {

    private List<FriendApplyRecord> mDataList;

    public ApplyNewFriendListPresenter(@NonNull Context context, @NonNull ApplyNewFriendListContract.View view) {
        super(context, view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void init() {

        UserInfo userInfo = UserManager.getInstance(mContext).getUserInfo();
        if (userInfo != null) {
            //头像，昵称，id
            String headerUrl = userInfo.getAvatarUrl();
            String nickName = userInfo.getNickName();
            String showId = userInfo.getShowId();
            mView.showHeaderData(headerUrl, TextUtils.isEmpty(nickName) ? "无" : nickName, showId);
            //个人二维码
            int length = DensityUtil.dip2px(mContext, 60);
            String qrCodeUrl = String.format("%s/userQrcode/index.html?showId=%s", BaseBizAppLike.getInstance().getH5Server(), showId);
            Logger.d("QrCodeUrl: " + qrCodeUrl);
            Bitmap bitmap = CodeUtils.createImage(qrCodeUrl, length, length, null);
            mView.showQRCodeImage(bitmap);
        }
        loadDataFromCache(true);
    }

    /**
     * 从本地数据库缓存里读取数据
     *
     * @param firstLoad true-表示缓存加载之后，会从服务端刷新数据；false-表示不继续从服务器加载新的数据
     */
    private void loadDataFromCache(final boolean firstLoad) {
        //查询本地数据库里的数据
        Flowable.just(0)
                .map(new Function<Integer, List<FriendApplyRecord>>() {
                    @Override
                    public List<FriendApplyRecord> apply(Integer integer) throws Exception {
                        List<FriendApplyRecord> list = FriendDbUtil.getApplyRecordList("desc");
                        if (list == null)
                            list = new ArrayList<>();
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<FriendApplyRecord>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<FriendApplyRecord>>() {
                    @Override
                    public void accept(List<FriendApplyRecord> list) throws Exception {
                        mDataList = list;
                        if (list != null && !list.isEmpty()) {
                            Logger.d("list.size==" + list.size());
                            mView.showMsgList(convertData(list));
                        } else {
                            mView.showMsgList(null);
                        }
                        if (firstLoad) {
                            loadDataFromServer();
                        } else {
                            resetRefreshState(null);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //理论从缓存里加载数据，不会出现异常
                        if (firstLoad) {
                            loadDataFromServer();
                        } else {
                            resetRefreshState("数据加载出错");
                        }
                    }
                });
    }

    /**
     * 进入本页面之后，第一次从服务端加载数据
     */
    private void loadDataFromServer() {
        GetApplyNewFriendListReq req = new GetApplyNewFriendListReq();
        String lastPullDate = CacheDataUtil.getLastApplyRecordPullDate(mContext);
        req.setLastReqDate(lastPullDate);
        MsgApi.getApplyNewFriendList(req)
                .compose(getProvider().<BaseResponse<FriendApplyRecordListBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FriendApplyRecordListBean>handleResponse())
                .map(new Function<FriendApplyRecordListBean, List<FriendApplyRecord>>() {
                    @Override
                    public List<FriendApplyRecord> apply(FriendApplyRecordListBean result) throws Exception {
                        List<FriendApplyRecord> list = result.getApplyRecordRespList();
                        //保存到数据库
                        FriendDbUtil.saveOrUpdateFriendApplyRecordList(list);

                        //删除
                        List<String> delList = result.getDelList();
                        if (delList != null && !delList.isEmpty()) {
                            for (String applyId : delList) {
                                int r = FriendDbUtil.deleteFriendApplyRecordByApplyId(applyId);
                                Logger.d("申请记录已被删除：" + r);
                            }
                        }

                        String lastPullDate = result.getLastReqDate();
                        //更新最近更新时间
                        CacheDataUtil.saveLastApplyRecordPullDate(mContext, lastPullDate);
                        return list != null ? list : new ArrayList<FriendApplyRecord>();
                    }
                })
                .subscribeWith(new CommSubscriber<List<FriendApplyRecord>>(mView) {
                    @Override
                    public void handleResult(List<FriendApplyRecord> list) {
                        if (list == null || list.isEmpty()) {
                            //说明没有新增数据
                            mView.hidePullDownRefresh();
                            mView.enableRefresh(true);
                        } else {
                            //刷新新的数据
                            loadDataFromCache(false);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        resetRefreshState(msg);
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

    private void resetRefreshState(String errMsg) {
        if (mDataList == null || mDataList.isEmpty()) {
            //显示数据加载失败，重试
            if (TextUtils.isEmpty(errMsg)) {
                mView.enableRefresh(true);
            } else {
                mView.enableRefresh(false);
            }
            mView.hidePullDownRefresh();
        } else {
            mView.enableRefresh(true);
            mView.hidePullDownRefresh();
            if (!TextUtils.isEmpty(errMsg)) {
                mView.toastErrorMessage(errMsg);
            }
        }
    }

    @Override
    public void getMsgList() {
        loadDataFromServer();
    }

    @Override
    public void deleteApplyRecord(final String applyId) {
        mView.showLoadingView();
        MsgApi.deleteApplyRecord(applyId)
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        //本地数据库删除该条数据
                        FriendDbUtil.deleteFriendApplyRecordByApplyId(applyId);
                        mView.removeData(applyId);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    private List<IApplyNewFriend> convertData(List<FriendApplyRecord> list) {
        if (list == null || list.size() == 0)
            return new ArrayList<>();
        List<IApplyNewFriend> dataList = new ArrayList<>();
        for (final FriendApplyRecord item : list) {
            dataList.add(new IApplyNewFriend() {
                @Override
                public String getIHeaderImg() {
                    return item.getAvatarUrl();
                }

                @Override
                public String getINick() {
                    return item.getNickName();
                }

                @Override
                public String getIContent() {
                    return item.getApplyMsg();
                }

                @Override
                public int getIStatus() {
                    return item.getStatus();
                }

                @Override
                public String getFriendId() {
                    return item.getFriendId();
                }

                @Override
                public String getApplyId() {
                    return item.getApplyId();
                }

                @Override
                public int getSexType() {
                    return item.getSex();
                }
            });
        }
        return dataList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAddFriend(AddFriendEvent event) {
        //刷新一下状态
        loadDataFromServer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDeleteFriend(DeleteFriendEvent event) {
        //删除数据
        FriendDbUtil.deleteFriendApplyRecordByUserId(event.friendId);
        mView.removeDataByFriendId(event.friendId);
    }

}