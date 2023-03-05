package org.jenkinsci.plugins.feishu;


import com.arronlong.httpclientutil.exception.HttpProcessException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import io.jenkins.cli.shaded.org.apache.commons.lang.StringUtils;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class FeishuNotifier extends Notifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeishuNotifier.class);
    private String webhookUrl;

    private String mentionedId;

    private String mentionedMobile;

    private String content;

    @DataBoundConstructor
    public FeishuNotifier(String webhookUrl, String mentionedId, String mentionedMobile, String content) {
        this.webhookUrl = webhookUrl;
        this.mentionedId = mentionedId;
        this.mentionedMobile = mentionedMobile;
        this.content = content;
    }

    public FeishuNotifier() {

    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getMentionedId() {
        return mentionedId;
    }

    public void setMentionedId(String mentionedId) {
        this.mentionedId = mentionedId;
    }

    public String getMentionedMobile() {
        return mentionedMobile;
    }

    public void setMentionedMobile(String mentionedMobile) {
        this.mentionedMobile = mentionedMobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        listener.getLogger().println("checking for pre-build");
        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        String req = toJSONString(build);
        listener.getLogger().println("推送通知" + req);
        //推送结束通知
        push(listener.getLogger(), webhookUrl, req);
        return true;
    }

    private String toJSONString(AbstractBuild<?, ?> build) {
        //组装内容
        StringBuilder content = new StringBuilder();
        //设置当前项目名称
        String projectName = build.getParent().getFullDisplayName();
        Result result = build.getResult();
        String useTimeString = build.getTimestampString();

        //控制台地址
        StringBuilder urlBuilder = new StringBuilder();
        String jenkinsUrl = NotificationUtil.getJenkinsUrl();
        if (StringUtils.isNotEmpty(jenkinsUrl)) {
            String buildUrl = build.getUrl();
            urlBuilder.append(jenkinsUrl);
            if (!jenkinsUrl.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append(buildUrl);
            if (!buildUrl.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append("console");
        }
        String consoleUrl = urlBuilder.toString();
        content.append("[feishu][" + projectName + "]" + getStatus(result));
        content.append("\n");
        content.append("构建用时：" + useTimeString + "\n");
        if (StringUtils.isNotEmpty(consoleUrl)) {
            content.append("[查看控制台](" + consoleUrl + ")");
        }
        content.append("\n\n");
        content.append(this.content);

        Map text = new HashMap<String, Object>();
        text.put("text", content.toString());

        Map data = new HashMap<String, Object>();
        data.put("msg_type", "text");
        data.put("content", text);
        //        {
        //            "msg_type": "text",
        //                "content": {
        //                    "text": "新更新提醒"
        //                }
        //        }
        String req = JSONObject.fromObject(data).toString();
        return req;
    }

    private void push(PrintStream logger, String url, String data) {
        String[] urls;
        if (url.contains(",")) {
            urls = url.split(",");
        } else {
            urls = new String[]{url};
        }
        for (String u : urls) {
            try {
                String msg = NotificationUtil.push(u, data);
                logger.println("通知结果" + msg);
            } catch (HttpProcessException | KeyManagementException | NoSuchAlgorithmException e) {
                logger.println("通知异常" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getStatus(Result result) {
        String[] successFaces = {
                "\uD83D\uDE0A", "\uD83D\uDE04", "\uD83D\uDE0E", "\uD83D\uDC4C", "\uD83D\uDC4D", "(o´ω`o)و", "(๑•̀ㅂ•́)و✧"
        };
        if (null != result && result.equals(Result.FAILURE)) {
            return "失败!!!\uD83D\uDE2D";
        } else if (null != result && result.equals(Result.ABORTED)) {
            return "中断!!\uD83D\uDE28";
        } else if (null != result && result.equals(Result.UNSTABLE)) {
            return "异常!!\uD83D\uDE41";
        } else if (null != result && result.equals(Result.SUCCESS)) {
            int max = successFaces.length - 1, min = 0;
            int ran = (int) (Math.random() * (max - min) + min);
            return "成功~" + successFaces[ran];
        }
        return "情况未知";
    }

    private String getBuildUrl(AbstractBuild<?, ?> build) {
        String getRootUrl = getDefaultURL();
        if (getRootUrl.endsWith("/")) {
            return getRootUrl + build.getUrl();
        } else {
            return getRootUrl + "/" + build.getUrl();
        }
    }

    private String getJobUrl(AbstractBuild<?, ?> build) {
        String getRootUrl = getDefaultURL();
        if (getRootUrl.endsWith("/")) {
            return getRootUrl + build.getProject().getUrl();
        } else {
            return getRootUrl + "/" + build.getProject().getUrl();
        }
    }

    private String getDefaultURL() {
        Jenkins instance = Jenkins.get();
        return instance.getRootUrl() != null ? instance.getRootUrl() : "";
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(FeishuNotifier.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "飞书消息通知";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/feishu-notification/help.html";
        }
    }
}
