package com.vivatech.onlinetutor.videochat.zoom;

public class ZoomConstants {

    /** Zoom create meeting type parameters
     * 1 - An instant meeting.
     * 2 - A scheduled meeting.
     * 3 - A recurring meeting with no fixed time.
     * 8 - A recurring meeting with fixed time.
     * 10 - A screen share only meeting.
     */
    public static final Integer CREATE_MEETING_TYPE_INSTANT = 1;
    public static final Integer CREATE_MEETING_TYPE_SCHEDULED = 2;
    public static final Integer CREATE_MEETING_TYPE_RECURRING = 3;
    public static final Integer CREATE_MEETING_TYPE_RECURRING_FIXED_TIME = 8;
    public static final Integer CREATE_MEETING_TYPE_SCREEN_SHARE_ONLY = 10;
    public static final Integer CREATE_MEETING_RECURRENCE_TYPE_DAILY = 1;

    public static final String ZOOM_DEFAULT_PASSWORD = "mumly@123";
    public static final String TIME_ZONE = "Africa/Nairobi";


}
