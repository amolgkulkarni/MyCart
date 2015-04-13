package eci.officeshopper.util;

public class Config {
    public enum AuthState {
        STATE_NONE,
        STATE_ACCEPTED,
        STATE_AUTHENTICATED,
        STATE_LOGGEDIN
    }

    // Constants
    public static String PREF_MODULE = "officeShopper";
    public static String DEALERS_URL = "http://qaf.ecinteractiveplus.com/mobile-branding/dealers/";
    public static String DEALER_LOGIN_API = "/v1/authentication/login";
    public static String CART_API = "/v1/cart";

    // Preferences
    public static String DEALER_AUTH_STATE = "DealerAuthState";
    public static String DEALER_BASE_URL = "DealerBaseUrl";
    public static String DEALER_LOGO_URL = "DealerLogoUrl";
    public static String DEALER_SPLASH_URL = "DealerSplashUrl";
    public static String USER_NAME = "Username";
    public static String USER_INFO = "UserInfo";
    public static String COOKIE_DOMAIN = "CookieDomain";

    // Cache
    public static String DEALER_LOGO_IMG = "dealerLogoImg";
    public static String DEALER_SPLASH_IMG = "dealerSplashImg";

    // Configuration
    public static Integer HOMESCREEN_NUM_COLUMNS = 3;
}
