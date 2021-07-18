package com.dd.auth.api.util;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AWSUtil {
    private static final Logger LOGGER = getLogger(AWSUtil.class);

    private final AmazonSNS snsClient;

    @Value("${aws.sns.arn: No Value}")
    private String snsARN;

    @Autowired
    public AWSUtil(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    public void publish(Map<String, String> snsMessage) {
        PublishRequest request = new PublishRequest();
        request.setMessage(snsMessage.toString());
        request.setSubject(snsMessage.get("Subject"));
        request.setTopicArn(snsARN);

        PublishResult result = snsClient.publish(request);

        LOGGER.debug("SNS message published messageId: {} ", result.getMessageId());
    }
}
