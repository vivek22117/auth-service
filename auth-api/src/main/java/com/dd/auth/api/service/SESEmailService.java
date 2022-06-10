package com.dd.auth.api.service;

import com.amazonaws.services.simpleemail.model.*;
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
public class SESEmailService {
    private static final Logger LOGGER = getLogger(SESEmailService.class);

    private final AWSUtil awsUtil;

    @Autowired
    public SESEmailService(AWSUtil awsUtil) {
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

    public void sendEmail(NotificationEmail notificationEmail) {

        String emailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Example HTML Email</title>\n" +
                "</head>\n" +
                "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n" +
                "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Dear example,</h5>\n" +
                "<p style=\"font-size: 16px; font-weight: 500\">Greetings from TutorialsBuddy</p>\n" +
                "<p>This is a simple html based email.</p>\n" +
                "</body>\n" +
                "</html>";

        String senderEmail = "vivek.mishra@doubledigit-solutions.com";
        String receiverEmail =  notificationEmail.getRecipient();
        String emailSubject = notificationEmail.getSubject();

        awsUtil.sendEmail(emailContent, senderEmail, receiverEmail, emailSubject);
    }

}