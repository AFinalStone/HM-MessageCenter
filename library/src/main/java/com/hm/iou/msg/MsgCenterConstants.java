package com.hm.iou.create;

/**
 * Created by hjy on 18/5/15.<br>
 */

public class Constants {

    /**
     * 吕约借条的优势
     */
    public static final String H5_URL_ELEC_BORROW_ADVANTAGE = "/IOU-LVY/borrow.html";

    /**
     * 吕约借条的准备信息
     */
    public static final String H5_URL_ELEC_BORROW_PREPARE_INFO = "/IOU-LVY/ready.html";

    /**
     * 吕约借条的附属条约1
     */
    public static final String H5_URL_ELEC_BORROW_APPEND_INFO = "/IOU-LVY/moreTreaty.html";

    /**
     * 吕约借条的附属条约2
     */
    public static final String H5_URL_ELEC_BORROW_APPEND_INFO_2 = "/IOU-LVY/moreTreaty2.html";

    /**
     * 吕约收条的使用场景
     */
    public static final String H5_URL_ELEC_RECEIVE_USE_SCENE = "/IOU-LVY/receive.html";


    /**
     * 吕约借条创建成功后的确认页面
     */
    public static final String H5_URL_ELEC_BORROW_CONFIRM = "/apph5/iou-econtract/app-contract.html?justiceId=";

    /**
     * 记债本新建页面防止软件闪退，20秒自动缓存的数据
     */
    public static final String KEY_DEBT_EDIT_CACHE = "ioucreate_key_debt_edit_cache";

    /**
     * 记债本编辑页面是否显示引导图遮罩
     */
    public static final String KEY_DEBT_EDIT_IS_HAVE_SHOW_MASK = "ioucreate_key_edit_is_have_show_mask";

    //临时保存电子借条的输入到数据库
    public static final String KEY_ELEC_BORROW_DRAFT_TMP_ID = "ElecBorrDraftCacheId";

    //会员类型为110时，表示是VIP
    public static final int MEMBER_VIP = 110;

    public static final int VIP_DEBT_MAX_COUNT = 2000;
    public static final int COMM_DEBT_MAX_COUNT = 1000;

    public static final int VIP_DEBT_MAX_IMAGE_COUNT = 9;
    public static final int COMM_DEBT_MAX_IMAGE_COUNT = 4;

    //VIP用户最小借款额可以为100
    public static final int BORROW_MONEY_VIP_MIN = 100;
    public static final int BORROW_MONEY_COMM_MIN = 500;
}
