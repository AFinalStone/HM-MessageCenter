package com.hm.iou.msg.widget;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.hm.iou.msg.R;

/**
 * Created by hjy on 18/4/28.<br>
 */

public class MsgCenterLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.msgcenter_layout_comm_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    public int getLoadEndRefreshView() {
        return R.id.tv_load_end_refresh;
    }
}
