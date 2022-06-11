package com.dd.auth.api.service;

import com.dd.auth.api.model.NotificationEmail;
import com.dd.auth.api.util.AWSUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SESEmailService {
    private static final Logger LOGGER = getLogger(SESEmailService.class);

    private final AWSUtil awsUtil;

    @Autowired
    public SESEmailService(AWSUtil awsUtil) {
        this.awsUtil = awsUtil;
    }

    public void sendEmail(NotificationEmail notificationEmail) {

        String senderEmail = "admin@doubledigit-solutions.com";
        String receiverEmail = notificationEmail.getRecipient();
        String emailSubject = notificationEmail.getSubject();

        awsUtil.sendEmail(notificationEmail.getBody(), senderEmail, receiverEmail, emailSubject);
    }

}
