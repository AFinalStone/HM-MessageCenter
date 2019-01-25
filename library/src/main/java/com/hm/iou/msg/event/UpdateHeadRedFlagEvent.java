package com.hm.iou.msg.event;

/**
 * @author syl
 * @time 2019/1/25 4:48 PM
 */
public class UpdateHeadRedFlagEvent {
    String redFlagCount;

    public UpdateHeadRedFlagEvent(String redFlagCount) {
        this.redFlagCount = redFlagCount;
    }
}
