package com.dd.auth.api.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.dd.auth.api.util.JwtConfiguration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class AWSClientConfig {

    private static final Logger logger = getLogger(AWSClientConfig.class);
    private static AWSCredentialsProvider awsCredentialsProvider;
    private final JwtConfiguration jwtConfiguration;

    @Value("${isRunningInEC2: No value}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal: No value}")
    private boolean isRunningInLocal;

    @Value("${awsRegion: No value}")
    private String region;

    public AWSClientConfig(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @Bean
    public AmazonSNS createSNSClient() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AmazonSNSClientBuilder.standard().
                    withCredentials(awsCredentialsProvider)
                    .withRegion(region)
                    .build();

        } catch (Exception ex) {
            logger.error("Exception Occurred while creating sns client" + ex.getMessage(), ex);
        }
        return null;
    }

    @Bean
    public AWSCognitoIdentityProvider createCognitoClient() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AWSCognitoIdentityProviderClientBuilder.standard()
                    .withCredentials(awsCredentialsProvider)
                    .withRegion(region)
                    .build();

        } catch (Exception ex) {
            logger.error("Exception Occurred while creating Cognito client" + ex.getMessage(), ex);
        }
        return null;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    private AWSCredentialsProvider getAWSCredentialProvider() {
        if (awsCredentialsProvider == null) {
            if (isRunningInEC2) {
                awsCredentialsProvider = new InstanceProfileCredentialsProvider(false);
            } else if (isRunningInLocal) {
                awsCredentialsProvider = new ProfileCredentialsProvider("admin");
            } else {
                awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();
            }
        }
        return awsCredentialsProvider;
    }
}
