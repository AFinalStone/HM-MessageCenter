package com.hm.iou.msg;

/**
 * @author syl
 * @time 2019/4/12 3:02 PM
 */
public class MsgCenterConstants {


    /**
     * 消息中心未读消息数量
     */
    public static final String KEY_UN_READ_MSG_NUM = "msgcenter_key_un_read_msg_num";


    //消息中心模块用的 SharedPreferences 文件名
    public static final String SP_MSG_CENTER = "msgcenter";

    /**
     * 最近一次获取好友的时间
     */
    public static final String KEY_LAST_FRIEND_UPDATE_TIME = "msgcenter_friend_update_time";

    /**
     * 最近一次获取好友申请记录的时间
     */
    public static final String KEY_LAST_FRIEND_APPLY_RECORD_TIME = "msgcenter_apply_record";

    public static final String KEY_DELTE_IM_FRIEND = "msgcenter_delelte_friend";

    // 通过邮箱上传（电子）凭证
    public static final String H5_URL_UPLOAD_PDF_BY_MEAIL = "/apph5/iou-case/alipay-receipt.html";

}
