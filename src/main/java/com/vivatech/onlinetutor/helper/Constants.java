package com.vivatech.onlinetutor.helper;

import com.vivatech.onlinetutor.config.ApplicationContextProvider;
import org.springframework.core.env.Environment;

public class Constants {

    public static final String EVENT_PROFILE_PICTURE = "EventPicture/";
    public static final String EVENT_COVER_PICTURE = "EventCover/";
    public static final String EVENT_BROCHURE = "EventBrochure/";
    public static final String PAGE_SIZE = "30";
    public static final Integer OTP_EXPIRY_TIME = 5;
    public static final String DEFAULT_COUNTRY = "KE";
    public static final Boolean SMS_TESTING;
    public static final String UPLOAD_DIR = "uploads";


    static {
        Environment env = ApplicationContextProvider.getApplicationContext().getBean(Environment.class);
        SMS_TESTING = env.getProperty("sms.testing", "false").equals("true");
    }



}
