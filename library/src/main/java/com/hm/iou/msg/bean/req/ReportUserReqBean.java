package com.hm.iou.msg.bean.req;

import lombok.Data;

/**
 * Created by hjy on 2019/4/11.
 */
@Data
public class ReportUserReqBean {

    private int feedbackId;
    private String fileId;
    private String memo;
    private String friendId;

}
