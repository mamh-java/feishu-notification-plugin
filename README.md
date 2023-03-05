# 飞书Jenkins消息通知插件

该插件适用于使用"飞书"工作的小伙伴，在Jenkins项目构建时使用群机器人进行状态通知

* 类似的插件有：

* https://github.com/mamh-java/qy-wechat-notification-plugin

* https://github.com/mamh-java/dingtalk-notification-plugin

* https://github.com/mamh-java/feishu-notification-plugin


这几个插件都经过我的修改，原版的我认为设计的不是特别的合理。

* 特别说明， 特别推荐在 流水线风格的job中来使用


当前插件版本号

* [2.38](https://github.com/mamh-java/feishu-notification-plugin/tree/feishu-notification-2.38-for-jenkins-2.303.1)




可以参考飞书 官方文档： [feishu](https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN?lang=zh-CN)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/4.png?raw=true)




# Freestyle Job配置

在自由分格的job中如何使用？ 选择 最后的  post-build 阶段 -> 添加 飞书消息通知 -> 配置 webhook url地址，配置消息内容



如下:

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/2.png?raw=true)


消息就会发送到特定的群里面， 自由风格job中消息内容 ，前 三行是插件里面写死的，
```
第一行是标题，一个关键字 feishu(关键字 是自定义机器人那里 安全设置 那里设置的关键字，这里写死 )， job name 和 成功 失败状态
第二行是 构建用时
第三行是 构建链接
第四行是  空白行，主要和下面消息主体内容 分割开的。
第五行 开始是消息主体。消息主体里面不能 解析 ${BUILD_URL} 这样的环境变量。

```



 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/3.1.png?raw=true)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/3.2.png?raw=true) 








## Pipeline Job参考配置
```
pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
                feishu content: """【feishu】Hello World

BUILD_URL = ${BUILD_URL}                

通过webhook将自定义服务的消息推送至飞书

                """, webhookUrl: 'https://open.feishu.cn/open-apis/bot/v2/hook/xxxx'
            }
        }
    }
}



```

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/5.1.png?raw=true)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/5.2.png?raw=true)

消息就会发送到特定的群里面， 流水线风格job中消息内容完全是可以自定义的，
```
特别注意 关键字 要和 自定义机器人那里 安全设置 那里设置的关键字 一致！！！

消息主体里面可以 解析 ${BUILD_URL} 这样的环境变量。

可以按照 消息标题 + 空行 + 消息主体内容 这样自定义通知内容

也可以在 post 阶段 里面的 failure 或者 success 代码块里面 发送不同内容的消息通知

```

# 如何添加群机器人

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.1.png)
 
 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.2.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.3.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.4.png)

![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.5.png)
 
 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.6.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.7.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.8.png)

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.9.png)














