package org.jenkinsci.plugins.feishu;

import com.arronlong.httpclientutil.exception.HttpProcessException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

public class FeishuStep extends Step {
    private static final Logger LOGGER = Logger.getLogger(FeishuStep.class.getName());

    private String webhookUrl;

    private String mentionedId;

    private String mentionedMobile;

    private String content;

    public FeishuStep(String webhookUrl, String mentionedId, String mentionedMobile, String content) {
        this.webhookUrl = webhookUrl;
        this.mentionedId = mentionedId;
        this.mentionedMobile = mentionedMobile;
        this.content = content;
    }

    @DataBoundConstructor
    public FeishuStep(String webhookUrl, String content) {
        this(webhookUrl, "", "", content);
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    @DataBoundSetter
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getMentionedId() {
        return mentionedId;
    }

    @DataBoundSetter
    public void setMentionedId(String mentionedId) {
        this.mentionedId = mentionedId;
    }

    public String getMentionedMobile() {
        return mentionedMobile;
    }

    @DataBoundSetter
    public void setMentionedMobile(String mentionedMobile) {
        this.mentionedMobile = mentionedMobile;
    }

    public String getContent() {
        return content;
    }

    @DataBoundSetter
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new FeishuStepExecution(stepContext, this);
    }

    public static class FeishuStepExecution extends SynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        private final transient FeishuStep step;

        protected FeishuStepExecution(@Nonnull StepContext context, FeishuStep step) {
            super(context);
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            TaskListener listener = getContext().get(TaskListener.class);
            String jsonString = toJSONString();
            if (StringUtils.isNotEmpty(step.getWebhookUrl())) {
                push(listener.getLogger(), step.getWebhookUrl(), jsonString);
            }
            return null;
        }

        private String toJSONString() {
            //组装内容
            StringBuilder content = new StringBuilder();
            content.append(step.getContent());
            Map text = new HashMap<String, Object>();
            text.put("text", content.toString());

            Map data = new HashMap<String, Object>();
            data.put("msg_type", "text");
            data.put("content", text);
            String req = JSONObject.fromObject(data).toString();
            return req;
        }

        private void push(PrintStream logger, String url, String data) {
            try {
                logger.println("will send msg" + data);
                String msg = NotificationUtil.push(url, data);
                logger.println("send msg result " + msg);
            } catch (HttpProcessException | KeyManagementException | NoSuchAlgorithmException e) {
                logger.println("send fail result " + e.getMessage());
                e.printStackTrace();
            }
        }

    }


    @Extension
    public static final class DescriptorImpl extends StepDescriptor {


        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> context = new HashSet<>();
            Collections.addAll(context, Run.class, TaskListener.class);
            return Collections.unmodifiableSet(context);
        }

        @Override
        public String getFunctionName() {
            return "feishu";
        }

        @Override
        public String getDisplayName() {
            return "FeishuStep";
        }


    }
}