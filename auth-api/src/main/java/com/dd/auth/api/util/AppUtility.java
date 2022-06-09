package com.dd.auth.api.util;

public class AppUtility {

    public static final long JWT_TOKEN_VALIDITY = 3600;


    public static final String API_AUTH_ROOT_URI = "/api";
    public static final String AUTH_SIGNUP_URI = "/public/auth/signup";
    public static final String ACCOUNT_VERIFICATION_TOKEN_URI = "/public/auth/accountVerification/{token}";
    public static final String AUTH_LOGIN_URI = "/public/auth/login";
    public static final String AUTH_REFRESH_TOKEN_URI = "/public/auth/refresh/token";
    public static final String AUTH_LOGOUT_URI = "/auth/logout";
    public static final String AUTH_CHANGE_PASSWORD_URI = "/auth/changepassword";
    public static final String AUTH_PUBLIC_KEY_URI = "/auth/.well-know/jwks.json";

}
