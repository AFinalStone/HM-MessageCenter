package com.hm.iou.msg.business.message.view.header;

/**
 * @author syl
 * @time 2019/4/9 10:14 AM
 */
public enum ModuleType {

    CONTRACT("msg_center_msg_list_header_contract", "合同消息"),
    BANK_CARD("msg_center_msg_list_header_need_to_back", "待还提醒"),
    EMAIL("msg_center_msg_list_header_similarity_contract", "疑似合同"),
    SIGHATURE_LIST("msg_center_msg_list_header_hm_msg", "管家消息"),;

    private String value;
    private String desc;

    ModuleType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
