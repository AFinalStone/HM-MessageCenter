package com.hm.iou.msg.bean.req;

import lombok.Data;

/**
 * Created by hjy on 2019/4/12.
 */
@Data
public class AddFriendReqBean {

    private String friendId;
    private String applyMsg;
    private int idType;//1用户id，2im_id

}
