package com.hm.iou.msg.business.friendlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.FriendDbUtil;
import com.hm.iou.database.table.FriendData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendListBean;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.msg.business.friendlist.view.FriendSection;
import com.hm.iou.msg.business.friendlist.view.IFriend;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.event.DeleteFriendEvent;
import com.hm.iou.msg.event.UpdateFriendEvent;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.StringUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class FriendListPresenter extends MvpActivityPresenter<FriendListContract.View> implements FriendListContract.Presenter {

    private static final String FIRST_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

    private List<IFriend> mDataList;

    public FriendListPresenter(@NonNull Context context, @NonNull FriendListContract.View view) {
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
        mView.showInitLoading();
        loadDataFromCache(true);
    }

    private void loadDataFromCache(final boolean firstLoad) {
        //查询本地数据库里的数据
        Flowable.just(0)
                .map(new Function<Integer, List<IFriend>>() {
                    @Override
                    public List<IFriend> apply(Integer integer) throws Exception {
                        List<FriendData> dataList = FriendDbUtil.getFriendList();
                        if (dataList == null)
                            dataList = new ArrayList<>();
                        List<IFriend> list = convertData(dataList);
                        Collections.sort(list, new PinyinComparator());
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<IFriend>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<IFriend>>() {
                    @Override
                    public void accept(List<IFriend> list) throws Exception {
                        mDataList = list;
                        if (list != null && !list.isEmpty()) {
                            mView.showMsgList(groupByPinyin(list));
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
        GetFriendListReq req = new GetFriendListReq();
        String lastPullDate = CacheDataUtil.getLastFriendPullDate(mContext);
        req.setLastReqDate(lastPullDate);
        MsgApi.getFriendList(req)
                .compose(getProvider().<BaseResponse<FriendListBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FriendListBean>handleResponse())
                .map(new Function<FriendListBean, Boolean>() {
                    @Override
                    public Boolean apply(FriendListBean result) throws Exception {
                        List<FriendData> list = result.getMailListRespList();

                        //保存到数据库
                        FriendDbUtil.saveOrUpdateFriendList(list);

                        //删除
                        List<String> delList = result.getDelList();
                        if (delList != null && !delList.isEmpty()) {
                            for (String uid : delList) {
                                int r = FriendDbUtil.deleteFriendByUserId(uid);
                                Logger.d("好友已被删除：" + r);
                            }
                        }

                        String lastPullDate = result.getLastReqDate();
                        //更新最近更新时间
                        CacheDataUtil.saveLastFriendPullDate(mContext, lastPullDate);

                        return (list == null || list.isEmpty()) && (delList == null || delList.isEmpty()) ? true : false;
                    }
                })
                .subscribeWith(new CommSubscriber<Boolean>(mView) {
                    @Override
                    public void handleResult(Boolean isEmpty) {
                        if (isEmpty) {
                            //说明没有新增数据
                            mView.hidePullDownRefresh();
                            mView.enableRefresh(true);
                            if (mDataList == null || mDataList.isEmpty()) {
                                mView.showDataEmpty();
                            } else {
                                mView.hideInitLoading();
                            }
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
                mView.showDataEmpty();
                mView.enableRefresh(true);
            } else {
                mView.showInitFailed();
                mView.enableRefresh(false);
            }
            mView.hidePullDownRefresh();
        } else {
            mView.hideInitLoading();
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

    /**
     * 进行数据转换
     *
     * @param list
     * @return
     */
    private List<IFriend> convertData(List<FriendData> list) {
        if (list == null || list.isEmpty())
            return new ArrayList<>();
        List<IFriend> dataList = new ArrayList<>();
        for (final FriendData item : list) {
            //拼音转换
            String name = TextUtils.isEmpty(item.getStageName()) ? item.getNickName() : item.getStageName();
            final String pinyinStr = Pinyin.toPinyin(name, "");
            dataList.add(new IFriend() {

                private String pinyin = pinyinStr;

                @Override
                public String getIHeaderImg() {
                    return item.getAvatarUrl();
                }

                @Override
                public String getINick() {
                    return !TextUtils.isEmpty(item.getStageName()) ? item.getStageName() : item.getNickName();
                }

                @Override
                public String getIAccount() {
                    return item.getFriendId();
                }

                @Override
                public String getPinyin() {
                    return pinyin == null ? "" : pinyin;
                }

            });
        }
        return dataList;
    }

    /**
     * 按姓名拼音排序
     */
    class PinyinComparator implements Comparator<IFriend> {

        @Override
        public int compare(IFriend o1, IFriend o2) {
            return o1.getPinyin().compareTo(o2.getPinyin());
        }
    }

    /**
     * 按拼音进行数据分组
     *
     * @param list
     * @return
     */
    private List<FriendSection> groupByPinyin(List<IFriend> list) {
        if (list == null || list.isEmpty())
            return null;
        Map<String, List<IFriend>> map = new LinkedHashMap<>();
        for (IFriend friend : list) {
            String pinyin = StringUtil.getUnnullString(friend.getPinyin());
            String firstLetter = (pinyin.length() > 0 ? pinyin.substring(0, 1) : "").toUpperCase();
            if (FIRST_LETTER.indexOf(firstLetter) == -1) {
                firstLetter = "#";
            }
            List<IFriend> sectionList = map.get(firstLetter);
            if (sectionList == null) {
                sectionList = new ArrayList<>();
                map.put(firstLetter, sectionList);
            }
            sectionList.add(friend);
        }
        List<FriendSection> dataList = new ArrayList<>();
        for (int i = 0; i < FIRST_LETTER.length(); i++) {
            String letter = FIRST_LETTER.substring(i, i + 1);
            List<IFriend> friendList = map.get(letter);
            if (friendList != null && !friendList.isEmpty()) {
                FriendSection header = new FriendSection(true, letter);
                dataList.add(header);
                for (IFriend friend : friendList) {
                    dataList.add(new FriendSection(friend));
                }
                header.setFriendList(friendList);
            }
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
        Logger.d("好友被删除了....");
        loadDataFromCache(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdateFriend(UpdateFriendEvent event) {
        loadDataFromServer();
    }

}