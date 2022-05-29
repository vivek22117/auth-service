package com.dd.auth.api.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.dd.auth.api.cognito.PasswordRequest;
import com.dd.auth.api.cognito.UserResponse;
import com.dd.auth.api.model.dto.RegisterRequest;
import com.dd.auth.api.util.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class CognitoAuthService {

    @Value(value = "${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value(value = "${aws.cognito.clientId}")
    private String clientId;

    private final AWSCognitoIdentityProvider cognitoClient;
    private final JsonUtility jsonUtility;


    public CognitoAuthService(AWSCognitoIdentityProvider cognitoClient, JsonUtility jsonUtility) {
        this.cognitoClient = cognitoClient;
        this.jsonUtility = jsonUtility;
    }

    public void cognitoUserSignUp(RegisterRequest request) {
        Collection<AttributeType> attributeTypeCollection = mapUserAttributes(request);

        try {

            attributeTypeCollection.add(new AttributeType().withName("email_verified").withValue("true"));


            AdminCreateUserRequest userRequest =
                    new AdminCreateUserRequest()
                            .withUserPoolId(userPoolId)
                            .withUsername(request.getUsername())
                            .withTemporaryPassword(request.getPassword())
                            .withUserAttributes(attributeTypeCollection)
                            .withMessageAction(MessageActionType.SUPPRESS)
                            .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                            .withForceAliasCreation(Boolean.TRUE);

            AdminCreateUserResult createUserResult = cognitoClient.adminCreateUser(userRequest);

            log.debug("User " + createUserResult.getUser().getUsername()
                    + " is created. Status: " + createUserResult.getUser().getUserStatus());

            AdminSetUserPasswordRequest adminSetUserPasswordRequest =
                    new AdminSetUserPasswordRequest()
                            .withUsername(request.getUsername())
                            .withUserPoolId(userPoolId)
                            .withPassword(request.getPassword()).withPermanent(true);

            cognitoClient.adminSetUserPassword(adminSetUserPasswordRequest);
        } catch (AWSCognitoIdentityProviderException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void changePassword(PasswordRequest passwordRequest) {

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
                .withAccessToken(passwordRequest.getAccessToken())
                .withPreviousPassword(passwordRequest.getOldPassword())
                .withProposedPassword(passwordRequest.getPassword());

        cognitoClient.changePassword(changePasswordRequest);
    }

    public String getUserInfo(String username) {
        AdminGetUserRequest userRequest = new AdminGetUserRequest()
                .withUsername(username)
                .withUserPoolId(userPoolId);
        try {
            AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);

            UserResponse userResponse = new UserResponse();
            userResponse.setUsername(userResult.getUsername());
            userResponse.setUserStatus(userResult.getUserStatus());
            userResponse.setUserCreateDate(userResult.getUserCreateDate());
            userResponse.setLastModifiedDate(userResult.getUserLastModifiedDate());

            List<AttributeType> userAttributes = userResult.getUserAttributes();
            for (AttributeType attribute : userAttributes) {
                if (attribute.getName().equals("custom:scope")) {
                    userResponse.setUserScope(attribute.getValue());
                } else if (attribute.getName().equals("email")) {
                    userResponse.setEmail(attribute.getValue());
                }
            }

            return jsonUtility.convertToString(userResponse);
        } catch (JsonProcessingException ex) {
            throw new UserNotFoundException("User info not found! Exception:- " + ex.getMessage());
        }
    }

    private Collection<AttributeType> mapUserAttributes(RegisterRequest request) {
        return Arrays.asList(
                new AttributeType().withName("email").withValue(request.getEmail()),
                new AttributeType().withName("name").withValue(request.getName()),
                new AttributeType().withName("phone_number").withValue(request.getMobile()),
                new AttributeType().withName("address").withValue(request.getAddress()),
                new AttributeType().withName("custom:scope").withValue("admin")
        );
    }
}
