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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

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

    @Value(value = "${aws.cognito.userPoolId}")
    private String userPoolId;

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
        return ((new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    }

    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(jwtConfiguration.getConnectionTimeout(),
                jwtConfiguration.getReadTimeout());
        URL jwkSetURL = new URL(jwtConfiguration.getJwkUrl());
        JWKSource keySource = new RemoteJWKSet<>(jwkSetURL, resourceRetriever);
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
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
