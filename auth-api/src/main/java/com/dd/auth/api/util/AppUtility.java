package com.dd.auth.api.util;

import org.slf4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class AppUtility {
    private static final Logger LOGGER = getLogger(AppUtility.class);

    public static final long JWT_TOKEN_VALIDITY = 3600;


    public static final String ENDPOINT_URL = "https://auth-api.cloud-interview.in";
    public static final String API_AUTH_ROOT_URI = "/api";
    public static final String AUTH_SIGNUP_URI = "/public/auth/signup";
    public static final String ACCOUNT_VERIFICATION_TOKEN_URI = "/public/auth/accountVerification/{token}";
    public static final String AUTH_LOGIN_URI = "/public/auth/login";
    public static final String AUTH_REFRESH_TOKEN_URI = "/public/auth/refresh/token";
    public static final String AUTH_LOGOUT_URI = "/auth/logout";
    public static final String AUTH_FORGOT_PASSWORD_URI = "/public/auth/forgot_password";
    public static final String AUTH_RESET_PASSWORD_URI = "/public/auth/reset_password";
    public static final String AUTH_PUBLIC_KEY_URI = "/auth/.well-know/jwks.json";

    public static String refreshTokenEmailContent(String refreshTokenUrl) {

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Password Reset Link!</title>\n" +
                "</head>\n" +
                "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n" +
                "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Dear example,</h5>\n" +
                "<p style=\"font-size: 16px; font-weight: 500\">Greetings from DoubleDigit-Solutions!</p>\n" +
                "<p>Thank you for signing up to DoubleDigit Cloud-Solutions, \n" +
                " please click on the below link to activate your account :</p>\n" +
                "<href>" + refreshTokenUrl + "</href>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String resetPasswordEmailContent(String resetPasswordUrl) {

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Password Reset Link!</title>\n" +
                "</head>\n" +
                "<body style=\"background: whitesmoke; padding: 30px; height: 100%\">\n" +
                "<h5 style=\"font-size: 18px; margin-bottom: 6px\">Dear XYZ,</h5>\n" +
                "<p style=\"font-size: 16px; font-weight: 500\">Greetings from DoubleDigit-Solutions!</p>\n" +
                "<p>Please use below link to reset your password.</p>\n" +
                "<href>" + resetPasswordUrl + "</href>\n" +
                "</body>\n" +
                "</html>";
    }


    public static boolean emailValidator(String email) {
        if (!email.equals("")) {
            final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = EMAIL_REGEX.matcher(email);
            return matcher.find();
        } else {
            LOGGER.error("Field email have a null value in it.",
                    new RuntimeException("At least one attributes returned a null value."));
            return false;
        }

    }

}
