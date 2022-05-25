package com.dd.auth.api.util;

public class AppUtility {

    public static final long JWT_TOKEN_VALIDITY = 3600;


    public static final String API_AUTH_ROOT_URI = "/api/auth";
    public static final String AUTH_SIGNUP_URI = "/signup";
    public static final String ACCOUNT_VERIFICATION_TOKEN_URI = "/accountVerification/{token}";
    public static final String AUTH_LOGIN_URI = "/login";
    public static final String AUTH_REFRESH_TOKEN_URI = "/refresh/token";
    public static final String AUTH_LOGOUT_URI = "/logout";
    public static final String AUTH_PUBLIC_KEY_URI = "/key";
}
