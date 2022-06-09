package com.dd.auth.api.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.dd.auth.api.exception.ApplicationException;
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
    private final AmazonSimpleEmailService emailService;

    @Value("${aws.sns.arn: No Value}")
    private String snsARN;

    @Autowired
    public AWSUtil(AmazonSNS snsClient, AmazonSimpleEmailService emailService) {
        this.snsClient = snsClient;
        this.emailService = emailService;
    }

    public void publish(Map<String, String> snsMessage) {
        PublishRequest request = new PublishRequest();
        request.setMessage(snsMessage.toString());
        request.setSubject(snsMessage.get("Subject"));
        request.setTopicArn(snsARN);

        PublishResult result = snsClient.publish(request);

        LOGGER.debug("SNS message published messageId: {} ", result.getMessageId());
    }

    public void sendEmail(String emailContent, String senderEmail, String receiverEmail, String emailSubject) {
        try {
            SendEmailRequest sendEmailRequest = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(receiverEmail))
                    .withMessage(new Message()
                            .withBody(new Body().withHtml(
                                    new Content().withCharset("UTF-8").withData(emailContent)))
                            .withSubject(new Content().withCharset("UTF-8").withData(emailSubject)))
                    .withSource(senderEmail);
            SendEmailResult sendEmailResult = emailService.sendEmail(sendEmailRequest);

            LOGGER.debug("SES message published messageId: {} ", sendEmailResult.getMessageId());
        } catch (Exception ex) {
            throw new ApplicationException("SES send email api call failed!" + ex.getMessage());
        }
    }
}
