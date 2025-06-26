package com.vivatech.onlinetutor.videochat.zoom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivatech.onlinetutor.dto.Response;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import com.vivatech.onlinetutor.exception.ZoomAPIExceptionHandler;
import com.vivatech.onlinetutor.helper.AppEnums;
import com.vivatech.onlinetutor.helper.CustomUtils;
import com.vivatech.onlinetutor.videochat.MeetingResponseDto;
import com.vivatech.onlinetutor.videochat.VideoChatDto;
import com.vivatech.onlinetutor.videochat.VideoChatInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class ZoomIntegrationImpl implements VideoChatInterface {
    private static final String BASE_URL = "https://zoom.us/";
    private static final String ZOOM_AUTH_TOKEN_URL = "oauth/token";
    private static final String ZOOM_CREATE_MEETING_URL = "v2/users/me/meetings";
    private static final String ZOOM_DELETE_MEETING_URL = "v2/meetings/";
    private static final String ACCOUNT_ID = "E7pnXWSHTgKJCuDKksAl6Q";
    private static final String GRANT_TYPE = "account_credentials";
    private static final String CLIENT_ID = "UE_4KDRJRFyfC7C1_DRl3w";
    private static final String CLIENT_SECRET = "A37ggkXe2Am87v5cCJQLIHadc2SkHh0j";

    @Autowired
    private RestClient restClient;

    @Override
    public boolean supports(AppEnums.MeetingAggregator aggregatorName) {
        return aggregatorName.equals(AppEnums.MeetingAggregator.ZOOM);
    }

    /**
     * Generates Zoom access token using account credentials
     * @return Access token string
     * @throws Exception if token generation fails
     */
    public String generateZoomAccessToken() {

        String requestBody = "grant_type=" + GRANT_TYPE + "&account_id=" + ACCOUNT_ID;

        String response = restClient.post()
                .uri(BASE_URL + ZOOM_AUTH_TOKEN_URL + "?" + requestBody)
                .header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (res, ctx) -> {
                    log.error("Getting 400 error while generating access token: {}", res.getMethod());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (res, ctx) -> {
                    log.error("Getting 500 error while generating access token: {}", res.getMethod());
                })
                .body(String.class);

        log.info("Zoom access token: {}", response);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // Extracting invoice_id from JSON
            String token = root.path("access_token").asText();

            // You can now use invoiceId as needed
            log.info("Access token for every zoom API request: {}", token);
            if (StringUtils.isEmpty(token)) throw new OnlineTutorExceptionHandler("Access token generation failed.");
            return token;
        } catch (Exception e) {
            throw new OnlineTutorExceptionHandler("Access token generation failed.");
        }
    }

    private String sendPostRequestToZoomAPIs(String requestBody, String url) {
        String accessToken = generateZoomAccessToken();
        return restClient
                .post()
                .uri(BASE_URL + url)
                .header("Authorization", "Bearer " + accessToken.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (res, ctx) -> {
                    String errorBody = new String(ctx.getBody().readAllBytes());
                    System.out.println("errorBody: " + errorBody);
                    log.error("Getting 400 error: {} | {} | {}", res.getMethod(), ctx.getStatusText(), errorBody);
                    throw new ZoomAPIExceptionHandler(errorBody);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (res, ctx) -> {
                    String errorBody = new String(ctx.getBody().readAllBytes());
                    log.error("Getting 500 error: {} | {} | {}", res.getMethod(), ctx.getStatusText(), errorBody);
                    throw new ZoomAPIExceptionHandler(errorBody);
                })
                .body(String.class);
    }

    private String sendGetRequestToZoomAPIs(String url, String queryParams, String method) {
        String accessToken = generateZoomAccessToken();
        return restClient
                .method(HttpMethod.valueOf(method))
                .uri(BASE_URL + url + (queryParams != null ? "?" + queryParams : ""))
                .header("Authorization", "Bearer " + accessToken.trim())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (res, ctx) -> {
                    String errorBody = new String(ctx.getBody().readAllBytes());
                    System.out.println("errorBody: " + errorBody);
                    log.error("Zoom - Getting 400 error: {} | {} | {}", res.getMethod(), ctx.getStatusText(), errorBody);
                    throw new ZoomAPIExceptionHandler(errorBody);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (res, ctx) -> {
                    String errorBody = new String(ctx.getBody().readAllBytes());
                    log.error("Zoom - Getting 500 error: {} | {} | {}", res.getMethod(), ctx.getStatusText(), errorBody);
                    throw new ZoomAPIExceptionHandler(errorBody);
                })
                .body(String.class);
    }

    @Override
    public Response createMeeting(VideoChatDto videoChatDto) {
        Date startTime = CustomUtils.convertLocalDateAndTimeToDate(
                videoChatDto.getMeetingStartDate(),
                videoChatDto.getMeetingStartTime()
        );
        ZoomCreateMeetingRequest request = ZoomCreateMeetingRequest.builder()
                .topic(videoChatDto.getMeetingTitle())
                .agenda(videoChatDto.getMeetingTitle())
                .defaultPassword(true)
                .password(ZoomConstants.ZOOM_DEFAULT_PASSWORD)
                .preSchedule(false)
                .startTime(startTime)
                .timezone(ZoomConstants.TIME_ZONE)
                .duration(videoChatDto.getMeetingDuration())
                .scheduleFor(videoChatDto.getHostEmail())
                .type(videoChatDto.isRecurring()
                        ? ZoomConstants.CREATE_MEETING_TYPE_RECURRING_FIXED_TIME
                        : ZoomConstants.CREATE_MEETING_TYPE_SCHEDULED)
                .recurrence(null)
                .build();
        if (videoChatDto.isRecurring()) {
            Date endTime = CustomUtils.convertLocalDateAndTimeToDate(
                    videoChatDto.getMeetingEndDate(),
                    videoChatDto.getMeetingEndTime()
            );
            ZoomRecurrenceDto recurrenceDto = new ZoomRecurrenceDto(endTime, ZoomConstants.CREATE_MEETING_TYPE_RECURRING_FIXED_TIME);
            request.setRecurrence(recurrenceDto);
        }

        String responseString = sendPostRequestToZoomAPIs(CustomUtils.makeDtoToJsonString(request), ZOOM_CREATE_MEETING_URL);
        log.info("Zoom Create meeting API response: {}", responseString);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseString);

            String meetingId = root.path("id").asText();
            String joinURL = root.path("join_url").asText();
            String hostURL = root.path("start_url").asText();

            MeetingResponseDto meetingResponse = MeetingResponseDto.builder()
                    .meetingId(meetingId)
                    .joinUrl(joinURL)
                    .hostUrl(hostURL)
                    .build();

            return Response.builder().status("SUCCESS").message("Meeting created.").data(meetingResponse).build();
        } catch (Exception e) {
            throw new OnlineTutorExceptionHandler("Meeting creation failed.");
        }
    }

    @Override
    public Response joinMeeting(VideoChatDto videoChatDto) {
        return null;
    }

    @Override
    public void deleteMeeting(VideoChatDto videoChatDto) {
        if (videoChatDto.getMeetingId().isBlank()) throw new OnlineTutorExceptionHandler("Meeting id is required.");
        sendGetRequestToZoomAPIs(ZOOM_DELETE_MEETING_URL + videoChatDto.getMeetingId(), null, "DELETE");
    }
}
