package com.vivatech.onlinetutor.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivatech.mumly_event.exception.CustomExceptionHandler;
import com.vivatech.onlinetutor.dto.PaginationResponse;
import com.vivatech.onlinetutor.exception.OnlineTutorExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class CustomUtils {

    public static String convertSecondToDurationString(long totalDurationInSeconds){
        // Convert the duration into hours, minutes, and seconds
        int hours = (int) (totalDurationInSeconds / 3600);
        int minutes = (int) ((totalDurationInSeconds % 3600) / 60);
        //int seconds = (int) (totalDurationInSeconds % 60);
        return String.format("%02d Hrs %02d Min", hours, minutes);
    }

    public static int convertDurationToMinute(long totalDurationInSeconds){
        return (int) (totalDurationInSeconds / 60);
    }

    public static Date getStartDateOfCurrentMonth() {
        // Create a Calendar instance and set it to the current date
        Calendar calendar = Calendar.getInstance();

        // Set the day of the month to 1 (the first day of the month)
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Set the time fields to zero (midnight) to get the start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Return the start date of the current month
        return calendar.getTime();
    }

    public static String formatLocalDateToString(LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    public static Date getEndDateOfCurrentMonth() {
        // Create a Calendar instance and set it to the current date
        Calendar calendar = Calendar.getInstance();

        // Set the day of the month to the maximum possible value for the current month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Set the time fields to the end of the day (23:59:59.999)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        // Return the end date of the current month
        return calendar.getTime();
    }

    // Function to get the start date of the previous month
    public static Date getPreviousMonthStartDate() {
        Calendar calendar = Calendar.getInstance();
        // Set the calendar to the first day of the previous month
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day
        calendar.add(Calendar.MONTH, -1); // Go back one month
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime(); // Return as java.util.Date
    }

    // Function to get the end date of the previous month
    public static Date getPreviousMonthEndDate() {
        Calendar calendar = Calendar.getInstance();
        // Set the calendar to the last day of the previous month
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of current month
        calendar.add(Calendar.MONTH, -1); // Go back one month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set to last day of that month
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime(); // Return as java.util.Date
    }

    public static Date last7DaysStartDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        log.info("Start date of the last 7 days: {}", startDate);
        return startDate;
    }

    public static Date addDaysToJavaUtilDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static Date addMinutesToJavaUtilDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static LocalDateTime addMinutesToJavaUtilDate(LocalDateTime date, int minutes) {
        return date.plusMinutes(minutes);
    }

    public static int calculateTimeLeftForExpiry(Date loginTime, int minutes) {
        // Calculate the expiry time by adding the expiry duration to the login time
        long expiryTimeInMillis = loginTime.getTime() + TimeUnit.MINUTES.toMillis(minutes);

        // Get the current time
        long currentTimeInMillis = System.currentTimeMillis();

        // Calculate the time left until expiry
        long timeLeftInMillis = expiryTimeInMillis - currentTimeInMillis;

        // If time left is negative, it means the token has expired
        return convertDurationToMinute(timeLeftInMillis > 0 ? TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) : 0);
    }

    public static String convertDateToString(Date date) {
        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Convert the Date to String using the defined format
        return dateFormat.format(date);
    }

    public static Date startDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date endDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static int calculateTheDurationBetweenTwoDates(Date startDate, Date endDate) {
        return convertDurationToMinute(Duration.between(startDate.toInstant(), endDate.toInstant()).getSeconds());
    }

    public static List<String> getDatesInMonth(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        int daysInMonth = ym.lengthOfMonth();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dates = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = ym.atDay(day); // Generate the date for the given day
            dates.add(date.format(formatter)); // Format and add to the list
        }

        return dates;
    }

    public static int getDayPartFromDate(String dateString) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the input date string into a LocalDate object
        LocalDate date = LocalDate.parse(dateString, formatter);

        // Extract the day part (dd)
        return date.getDayOfMonth();
    }

    public static String getDayFromDate(String dateString) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the input date string into a LocalDate object
        LocalDate date = LocalDate.parse(dateString, formatter);

        // Get the day of the week
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Return the name of the day (e.g., MONDAY, TUESDAY)
        return dayOfWeek.toString();
    }

    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            throw new OnlineTutorExceptionHandler("Date cannot be null");
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertLocalDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static long convertDecimalToSeconds(double decimalValue) {
        if (decimalValue % 0.25 != 0) {
            throw new OnlineTutorExceptionHandler("The decimal value must be in multiples of 0.25");
        }
        // 1 hour = 60 minutes
        return (long) (decimalValue * 60 * 60);
    }

    public static String addDurationToTime(String startTime, int durationInMinutes) {
        // Parse the starting time from string
        LocalTime time = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));

        // Add the duration to the starting time
        time = time.plusMinutes(durationInMinutes);

        // Format the new time as "HH:mm" for 24-hour format
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static int calculateWeekdays(Date startDate, Date endDate) {
        // Ensure startDate is before endDate
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date should not be after end date.");
        }

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(startDate);
        end.setTime(endDate);

        int weekdays = 0;

        // Iterate from start date to end date
        while (!start.after(end)) {
            int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);

            // Exclude Saturday (7) and Sunday (1)
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                weekdays++;
            }

            // Move to the next day
            start.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weekdays;
    }

    public static Date convertLocalDateAndTimeToDate(LocalDate date, String time) {
        if (date == null || time.isEmpty()) {
            throw new CustomExceptionHandler("Date and time must not be null or empty");
        }

        // Parse time string to LocalTime
        LocalTime localTime = LocalTime.parse(time); // default format is HH:mm

        // Combine LocalDate and LocalTime into LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(date, localTime);

        // Convert LocalDateTime to java.util.Date
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static String removeJsonDelimiters(String jsonRequest) {
        if (jsonRequest == null || jsonRequest.isEmpty()) {
            return jsonRequest; // Return as is if input is null or empty
        }
        return jsonRequest.replaceAll("[{}\\[\\]\":,]", "").trim();
    }


    public static String makeDtoToJsonString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return jsonInString;
    }

    public static <T> T readJsonStringToObject(Object jsonInString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonInString.toString(), clazz);
        } catch (JsonProcessingException e) {
            throw new CustomExceptionHandler(e.getMessage());
        }
    }


    public static <T> Page<T> convertListToPage(List<T> dataList, int pageNumber, int pageSize) {

        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, dataList.size());

        List<T> pageContent = dataList.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(pageNumber, pageSize), dataList.size());
    }

    public static <T> PaginationResponse<T> convertPageToPaginationResponse(Page<T> page, List<T> dtoList) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setContent(page.getContent());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        return response;
    }

    public static String generateRandomString() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static Map<String, Double> initializeEmptyMonthMap() {
        Map<String, Double> emptyMap = new LinkedHashMap<>();
        emptyMap.put("Jan", 0.0);
        emptyMap.put("Feb", 0.0);
        emptyMap.put("Mar", 0.0);
        emptyMap.put("Apr", 0.0);
        emptyMap.put("May", 0.0);
        emptyMap.put("Jun", 0.0);
        emptyMap.put("Jul", 0.0);
        emptyMap.put("Aug", 0.0);
        emptyMap.put("Sep", 0.0);
        emptyMap.put("Oct", 0.0);
        emptyMap.put("Nov", 0.0);
        emptyMap.put("Dec", 0.0);
        return emptyMap;
    }

    public static LocalTime convertStringToTime(String time) {
        if (time == null || time.isEmpty()) return null;
        //check the patter is in the format 00:00:00
        Pattern pattern = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$");
        Matcher matcher = pattern.matcher(time);
        if (!matcher.matches()) {
            throw new OnlineTutorExceptionHandler("Invalid time format. Expected format is HH:mm:ss");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.parse(time, formatter);
        return localTime;
    }
}
