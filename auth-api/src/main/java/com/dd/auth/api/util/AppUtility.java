package com.dd.auth.api.util;

public class AppUtility {

    public static final String API_KEY = "1236405a5eb81bcefbf9ae0e9b9b226c";

    public static final String WEATHER_ROOT_URL = "http://api.openweathermap.org/data/2.5/weather?";
    public static final long JWT_TOKEN_VALIDITY = 3600;

    public static final String ADMIN_ROOT_API = "/api/admin";
    public static final String ADMIN_API_CITY_HISTORY = "/city/history";
    public static final String ADMIN_API_USER_HISTORY = "/user/history";

    public static final String AUTH_API_ROOT_MAPPING = "/api/auth";
    public static final String SIGNUP_AUTH_API = "/signup";
    public static final String LOGIN_AUTH_API = "/login";

    public static final String WEATHER_API_ROOT_MAPPING = "/api/weather";
    public static final String WEATHER_CITY_API = "/{city}";
    public static final String WEATHER_HISTORY_API = "/history";
    public static final String WEATHER_DELETE_API = "/delete";
    public static final String WEATHER_DELETE_ALL_API = "/deleteAll/{username}";


}
