# 飞书Jenkins消息通知插件

This plugin allows sending message notification to your work group with feishu, 
and help you understand the build result.

[2.38](https://github.com/mamh-java/feishu-notification-plugin/tree/feishu-notification-2.38-for-jenkins-2.303.1)


## How to use
In order to support Jenkins notification configuration and Webhook address configuration, 
you need to install the official Jenkins plugin

**1. Log in feishu，invite Jenkins CI Assistant(this is a chat bot) to join the group.**

**2. add 自定义机器人指南 in the group, and get the Web Hook URL for receiving the web jobs status notification**

[feishu](https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN?lang=zh-CN)

**3. Configure the Jenkins plugin**
 - Select the Job in Jenkins and configure the plugin and Webhook address, configure
 -- post-build operations
 -- add post-build steps
 -- select  飞书消息通知   configuration

![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/1.png)

![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/2.png?raw=true)

 - Bot sends the build result to the specified group, 
   clicks the card to the applet to view the build
   details, and the applet records the job list

![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/3.1.png?raw=true)

![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/3.2.png?raw=true) 

 ![](https://cdn.jsdelivr.net/gh/mamh-java/feishu-notification-plugin@feishu-notification-2.38-for-jenkins-2.303.1/static/4.png?raw=true)

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



