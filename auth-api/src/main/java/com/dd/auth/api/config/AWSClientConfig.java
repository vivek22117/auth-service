package com.dd.auth.api.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.dd.auth.api.exception.NotFoundException;
import com.dd.auth.api.model.AwsSecrets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class AWSClientConfig {
    private static final Logger logger = getLogger(AWSClientConfig.class);

    private final static int INITIAL_POOL_SIZE = 10;
    private final static int MAX_POOL_SIZE = 50;
    private final static int CONNECTION_TIMEOUT = 2000;

    private static AWSCredentialsProvider awsCredentialsProvider;

    @Value("${isRunningInEC2: No value}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal: No value}")
    private boolean isRunningInLocal;

    @Value("${awsRegion: No value}")
    private String region;

    @Bean
    public Gson getGson() {
        return new Gson();
    }

    @Bean
    public DataSource dataSource(AWSSecretsManager awsSecretsManager, Gson gson) {
        HikariConfig config = new HikariConfig();

        if (isRunningInLocal) {
            config.setUsername("sa");
            config.setPassword("");
            config.setJdbcUrl("jdbc:h2:file:C:/auth/auth-db");
        } else {
            AwsSecrets secrets = getSecret(awsSecretsManager, gson);
            assert secrets != null;
            config.setUsername(secrets.getUsername());
            config.setPassword(secrets.getPassword());
            config.setJdbcUrl("jdbc:" + secrets.getEngine() + "://" + secrets.getHost() + "/auth_service");
        }

        config.setMinimumIdle(INITIAL_POOL_SIZE);
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);

        return new HikariDataSource(config);
    }

    @Bean
    public AmazonSNS createSNSClient() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AmazonSNSClientBuilder.standard().
                    withCredentials(awsCredentialsProvider)
                    .withRegion(Regions.US_EAST_1)
                    .build();

        } catch (Exception ex) {
            logger.error("Exception Occurred while creating sns client" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Bean
    public AWSSecretsManager createSecretsManager() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AWSSecretsManagerClientBuilder.standard().
                    withCredentials(awsCredentialsProvider)
                    .withRegion(Regions.US_EAST_1)
                    .build();

        } catch (Exception ex) {
            logger.error("Exception Occurred while creating aws secrets-manager client" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(awsCredentialsProvider)
                    .withRegion(Regions.US_EAST_1)
                    .build();
        } catch (Exception ex) {
            logger.error("Exception Occurred while creating ses client" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Bean
    public AWSCognitoIdentityProvider createCognitoClient() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AWSCognitoIdentityProviderClientBuilder.standard()
                    .withCredentials(awsCredentialsProvider)
                    .withRegion(Regions.US_EAST_1)
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

    private AwsSecrets getSecret(AWSSecretsManager client, Gson gson) {

        String secretName = "auth/service/db-credentials";

        String secret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception ex) {
            logger.error("Specified " + secretName + "secret not found!");
            throw new NotFoundException("The specified aws secret not found!");
        }
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            return gson.fromJson(secret, AwsSecrets.class);
        }

        return null;
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
