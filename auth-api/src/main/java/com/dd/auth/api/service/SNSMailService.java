package com.dd.auth.api.service;

import com.dd.auth.api.exception.ApplicationException;
import com.dd.auth.api.model.NotificationEmail;
import com.dd.auth.api.util.AWSUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SNSMailService {
    private static final Logger LOGGER = getLogger(SNSMailService.class);

    private final AWSUtil awsUtil;

    @Autowired
    public SNSMailService(AWSUtil awsUtil) {
        this.awsUtil = awsUtil;
    }

    void sendMail(NotificationEmail notificationEmail) {
        Map<String, String> snsMessage = new HashMap<>();

        snsMessage.put("From", "vivek.mishra@doubledigit-solutions.com");
        snsMessage.put("To", notificationEmail.getRecipient());
        snsMessage.put("Subject", notificationEmail.getSubject());
        snsMessage.put("Text", notificationEmail.getBody());

        try {
            awsUtil.publish(snsMessage);
            LOGGER.info("Activation mail sent!!");
        } catch (Exception ex) {
            throw new ApplicationException("Exception occurred when sending mail to " + notificationEmail.getRecipient());
        }
    }
}
