#### HM-MessageCenter

集成了帮助中心，意见反馈，历史反馈，消息中心等功能模块

#### 功能介绍

- 帮助中心页面
- 意见反馈页面
- 反馈详情页面
- 历史反馈页面
- 消息中心页面

#### 安装

在工程根目录的build.gradle文件里添加如下maven地址：

```gradle
allprojects {
    repositories {
        ...
        maven { url 'file:///Users/syl/.m2/repository/' }
        ...
    }
}
```

在项目模块的buile.gradle文件里面加入如下依赖：

```gradle
    compile 'com.heima.iou:hmmessagecenterlocal:1.0.0'
```

外部引用：

```gradle
    compile 'com.heima.iou:hmbasebizlocal:1.0.0'
    compile 'com.heima.iou:hmdbcenterlocal:1.0.0'
```

#### 使用说明

支持的路由

| 页面 | 路由url | 备注 |
| ------ | ------ | ------ |
| 帮助中心页面 | ```hmiou://m.54jietiao.com/message/helpcenter```| |
| 意见反馈页面 | ```hmiou://m.54jietiao.com/message/feedback```|   |
| 历史反馈页面 | ````hmiou://m.54jietiao.com/message/feedback_history?type=*```| type的类型只能是FeedbackKind枚举类型中的一种|
| 反馈详情页面 | ```hmiou://m.54jietiao.com/message/feedback_detail?feedback_id=*```| |
| 消息中心页面 | ```hmiou://m.54jietiao.com/message/index```  |

路由文件

```json
{
  "message": [
    {
      "url": "hmiou://m.54jietiao.com/message/index",
      "iclass": "",
      "aclass": "com.hm.iou.hmreceipt.ui.activity.MessageCenterActivity"
    },
    {
      "url": "hmiou://m.54jietiao.com/message/helpcenter",
      "iclass": "",
      "aclass": "com.hm.iou.msg.business.HelpCenterActivity"
    },
    {
      "url": "hmiou://m.54jietiao.com/message/feedback",
      "iclass": "",
      "aclass": "com.hm.iou.msg.business.feedback.view.FeedbackActivity"
    },
    {
      "url": "hmiou://m.54jietiao.com/message/feedback_history?type=*",
      "iclass": "",
      "aclass": "com.hm.iou.msg.business.feedback.view.HistoryFeedbackActivity"
    },
    {
      "url": "hmiou://m.54jietiao.com/message/feedback_detail?feedback_id=*",
      "iclass": "",
      "aclass": "com.hm.iou.msg.business.feedback.view.FeedbackDetailActivity"
    }
  ]
  }
```

#### 集成说明

- 集成本模块之前，需要保证一下模块已经初始化：

Logger（日志记录），HM-BaseBiz初始化(基础业务模块)，HM-Network（网络框架），HM-Router（路由模块）
，HM-DBCenter(数据库缓存框架)

- 在使用本模块之前，需要进行MsgCenterAppLike的初始化

```java
        MsgCenterAppLike msgCenterAppLike = new MsgCenterAppLike();
        msgCenterAppLike.onCreate(this);
```

#### Author

syl