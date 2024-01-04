package com.aimelive.ucs.core.schedulers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = ArticlesSchedulerTask.Config.class)
@Component(service = Runnable.class)
public class ArticlesSchedulerTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ObjectClassDefinition(name = "Articles Import Scheduled Task", description = "Importing articles from csv")
    public @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "*/30 * * * * ?";
        // String scheduler_expression() default "0 */30 * * * ?";

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(name = "A parameter", description = "Can be configured in /system/console/configMgr")
        String myParameter() default "";
    }

    private String myParameter;

    @Override
    public void run() {
        logger.debug("ARTICLES IMPORT SCHEDULAR THAT RUN EVERY  30 SECONDS");

        try {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("admin", "admin"));

            // Create an HttpClient with Basic Authentication
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();
            HttpPost httpPost = new HttpPost("http://localhost:4502/bin/servlets/file-upload");

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            String responseBody = EntityUtils.toString(entity);

            logger.debug("THE API RESPONSE: " + responseBody);

        } catch (Exception e) {
            logger.error("Error while making POST request: {}", e.getMessage(), e);
        }
    }

    @Activate
    protected void activate(final Config config) {
        myParameter = config.myParameter();
    }
}
