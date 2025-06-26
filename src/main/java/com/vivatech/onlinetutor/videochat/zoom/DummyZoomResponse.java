package com.vivatech.onlinetutor.videochat.zoom;

public class DummyZoomResponse {

    public String accessTokenResponse() {
        return "{\n" +
                "    \"access_token\": \"eyJzdiI6IjAwMDAwMiIsImFsZyI6IkhTNTEyIiwidiI6IjIuMCIsImtpZCI6IjE5ODI1MmVmLWY0NjAtNGJiNC1hN2I3LTk5NTc1OWQ2MGExNyJ9.eyJhdWQiOiJodHRwczovL29hdXRoLnpvb20udXMiLCJ1aWQiOiJaemVzSm5lX1RCMmxKd3dnZlAxT1Z3IiwidmVyIjoxMCwiYXVpZCI6ImZhZDdjMDNmNGM3ZmI0MzZkZWUyNDFiYTNlYjBlMDIxZWNiNDdjOTM4MDMxOGEzZGVlMjJlNGEzYTFhNGIzNmMiLCJuYmYiOjE3NTA4NDExMzMsImNvZGUiOiJvdC1Nc2NXS1IycVZaZG4xVk9JUHdRREM4ZXpJb3oyMlUiLCJpc3MiOiJ6bTpjaWQ6VUVfNEtEUkpSRnlmQzdDMV9EUmwzdyIsImdubyI6MCwiZXhwIjoxNzUwODQ0NzMzLCJ0eXBlIjozLCJpYXQiOjE3NTA4NDExMzMsImFpZCI6IkU3cG5YV1NIVGdLSkN1REtrc0FsNlEifQ.892BX-YY_QclW20CCydXBYLBS8VGXx_IxIOs_bggVsQZ3GgfhT5ZX5huZ8qahx7ax18fTsmKE0vr41js4w7mFA\",\n" +
                "    \"token_type\": \"bearer\",\n" +
                "    \"expires_in\": 3599,\n" +
                "    \"scope\": \"user:read:user:admin user:update:user:admin user:read:clocked_in_user:admin meeting:read:list_meetings:admin meeting:write:meeting:admin meeting:update:meeting:admin meeting:read:list_polls:admin meeting:read:invitation:admin meeting:write:invite_links:admin meeting:read:token:admin meeting:write:open_app:admin group:read:meeting_template_detail:admin group:read:meeting_template_detail:master webinar:write:webinar:admin webinar:write:webinar:master webinar:write:poll:master\",\n" +
                "    \"api_url\": \"https://api.zoom.us\"\n" +
                "}";
    }

    public String createMeetingResponseRecurringFalse() {
        return "{\"uuid\":\"R2jVen2cQpuf9dGBl7VykA==\"," +
                "\"id\":99355477266," +
                "\"host_id\":\"ZzesJne_TB2lJwwgfP1OVw\"," +
                "\"host_email\":\"javed@vivatechrnd.com\"," +
                "\"topic\":\"Test meeting 25-06-2025\"," +
                "\"type\":2,\"status\":\"waiting\"," +
                "\"start_time\":\"2025-06-25T12:33:17Z\"," +
                "\"duration\":60,\"timezone\":\"Africa/Nairobi\"," +
                "\"agenda\":\"Test meeting 25-06-2025\"," +
                "\"created_at\":\"2025-06-25T12:33:17Z\"," +
                "\"start_url\":\"https://zoom.us/s/99355477266?zak=eyJ0eXAiOiJKV1QiLCJzdiI6IjAwMDAwMiIsInptX3NrbSI6InptX28ybSIsImFsZyI6IkhTMjU2In0.eyJpc3MiOiJ3ZWIiLCJjbHQiOjAsIm1udW0iOiI5OTM1NTQ3NzI2NiIsImF1ZCI6ImNsaWVudHNtIiwidWlkIjoiWnplc0puZV9UQjJsSnd3Z2ZQMU9WdyIsInppZCI6ImU2YzYwNTg4MzMxZDRiMDA4ODNmMDhjZjJlYTMxY2IxIiwic2siOiIwIiwic3R5IjoxMDAsIndjZCI6ImF3MSIsImV4cCI6MTc1MDg2MTk5NywiaWF0IjoxNzUwODU0Nzk3LCJhaWQiOiJFN3BuWFdTSFRnS0pDdURLa3NBbDZRIiwiY2lkIjoiIn0.BtHEYqkPep4LLLd0i9xqCfa2_0-FQ5cwyS1-lGryxlI\",\"join_url\":\"https://zoom.us/j/99355477266?pwd=DpRZvK0NAFeYK72UWqUeWp91r01x5L.1\"," +
                "\"password\":\"123456\",\"h323_password\":\"123456\"," +
                "\"pstn_password\":\"123456\"," +
                "\"encrypted_password\":\"DpRZvK0NAFeYK72UWqUeWp91r01x5L.1\"," +
                "\"settings\":{\"host_video\":true,\"participant_video\":true,\"cn_meeting\":false,\"in_meeting\":false,\"join_before_host\":true,\"jbh_time\":0,\"mute_upon_entry\":false,\"watermark\":false,\"use_pmi\":false,\"approval_type\":2,\"audio\":\"voip\",\"auto_recording\":\"none\",\"enforce_login\":false,\"enforce_login_domains\":\"\",\"alternative_hosts\":\"\",\"alternative_host_update_polls\":false,\"close_registration\":false,\"show_share_button\":false,\"allow_multiple_devices\":false,\"registrants_confirmation_email\":true,\"waiting_room\":false,\"request_permission_to_unmute_participants\":false,\"registrants_email_notification\":true,\"meeting_authentication\":false,\"encryption_type\":\"enhanced_encryption\",\"approved_or_denied_countries_or_regions\":{\"enable\":false},\"breakout_room\":{\"enable\":false},\"internal_meeting\":false,\"continuous_meeting_chat\":{\"enable\":true,\"auto_add_invited_external_users\":false,\"auto_add_meeting_participants\":false,\"channel_id\":\"web_sch_d969ffa31f0649ffb91be97007924ab7\"},\"participant_focused_meeting\":false,\"push_change_to_calendar\":false,\"resources\":[],\"allow_host_control_participant_mute_state\":false,\"alternative_hosts_email_notification\":true,\"show_join_info\":false,\"device_testing\":false,\"focus_mode\":false,\"meeting_invitees\":[],\"private_meeting\":false,\"email_notification\":true,\"host_save_video_order\":false,\"sign_language_interpretation\":{\"enable\":false},\"email_in_attendee_report\":false},\"creation_source\":\"open_api\",\"pre_schedule\":false}";
    }
}
