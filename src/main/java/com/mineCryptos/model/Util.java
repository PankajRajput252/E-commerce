package com.mineCryptos.model;

import com.mineCryptos.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Util {


    public static boolean isDefined(Collection collection) {
        boolean isDefined = Boolean.FALSE;
        if (null != collection && !collection.isEmpty()) {
            isDefined = Boolean.TRUE;
        }
        return isDefined;

    }

    public static boolean isDefined(final Integer value) {
        return (null!= value && value!=0);
    }

    public static String  getCurrentUTCTimestampString() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        return utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public static int compareDate(String givenDate, String compareToDate) {
//        Added by on 29Feb2020 to check effective datetime can't be null
        if (!isDefined(givenDate)) {
            return 1;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            cal1.setTime(sdf.parse(givenDate));
            cal2.setTime(sdf.parse(compareToDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = cal2.compareTo(cal1);
        if (i > 0) {
            return -1; // givenDate is less than compareToDate
        } else if (i < 0) {
            return 1; // givenDate is greater than compareToDate
        } else {
            return 0; // givenDate is equal to compareToDate
        }

    }

    public static boolean isDefined(final String value) {
        return ((value != null) && (value.length() > 0) && (!value.equalsIgnoreCase("null")) && (!value.equalsIgnoreCase("false")));
    }

    public static FinalResponse setSuccessMessage(FinalResponse finalResponse) {
        finalResponse.setTimeStamp(System.currentTimeMillis());
        finalResponse.setStatusCode("200");
        finalResponse.setStatus("SUCCESS");
        return finalResponse;
    }

    public static FinalResponse setMessage(FinalResponse finalResponse, String statusCode, String status) {
        finalResponse.setTimeStamp(System.currentTimeMillis());
        finalResponse.setStatusCode(statusCode);
        finalResponse.setStatus(status);
        return finalResponse;
    }

    public static void setCommonDefaultAttributes(StandardFieldClass standardFieldClass) {
        standardFieldClass.setCreatedDatetime(getCurrentUTCTimestampString());
        standardFieldClass.setLastModifiedDateTime(getCurrentUTCTimestampString());
    }

    public static String getEffectiveDateTime(String effectiveDateTime) {
        if (!Util.isDefined(effectiveDateTime)) {
            effectiveDateTime = getCurrentUTCTimestampString();
        }
        return effectiveDateTime;
    }

    public static Pageable getPageable(int size, int page) {
        Pageable pageable = null;
        if (0 < size) {
            pageable = PageRequest.of(page, size);
        }
        return pageable;
    }

    public static boolean isDefined(final UUID value) {
        return (null!= value);
    }

    public static UUID convertBinaryToHex(Object inputId) {
        UUID hexRepresentation = null;
        if (null != inputId && !inputId.equals("") && !inputId.equals("null")) {
            String binaryId = inputId.toString();
            if (binaryId.startsWith("0x")) { // Added by Akshay on 23Nov2019
                StringBuilder stringBuilder = new StringBuilder(binaryId);
                stringBuilder.replace(0, 2, ""); //removes 0x
                stringBuilder.insert(8, "-");
                stringBuilder.insert(13, "-");
                stringBuilder.insert(18, "-");
                stringBuilder.insert(23, "-");
                hexRepresentation = UUID.fromString(stringBuilder.toString());
            } else {
                /**
                 * Added by Akshay on 23Nov2019
                 * This method converts the given hex String(FFC052E7-9CA1-4DD7-AAEF-92BBFAF76D30)
                 * into binary representation 0xFFC052E79CA14DD7AAEF92BBFAF76D30
                 * @param hex ,the hex String id to be converted to binary representation
                 * @return hex representation of the given hex String
                 */
                binaryId = binaryId.toUpperCase();
                hexRepresentation = UUID.fromString(binaryId);
            }
        }
        return hexRepresentation;
    }

    public static String parseDateTimeAsLocalDateTime(String passedTime) {
        String formattedTime = null;
        if (null != passedTime) {
            if (passedTime.contains("T")) {
                formattedTime = passedTime.replace("T", " "); // replace the T with space
                if (formattedTime.length() == 19) { //2019-11-22 05:28:02  i.e missing .000
                    formattedTime = formattedTime + ".000"; // add .000 at the end
                }

            } else {
                formattedTime = passedTime; // return the passed string
            }


        }
        return formattedTime;
    }

    public static FinalResponse setUnSuccessMessage(FinalResponse finalResponse) {
        finalResponse.setTimeStamp(System.currentTimeMillis());
        finalResponse.setStatusCode("100");
        finalResponse.setStatus("FAILED");
        return finalResponse;
    }

    public static String convertHexToBinaryWith0xString(Object input) {
        Object binaryRepresentation = null;
        if (isDefined(input)) {
            String hexIdStr = String.valueOf(input);
            /*check added on 24Apr to return as it is if hexId is binary value*/
            if(hexIdStr.startsWith("0x"))
                return hexIdStr;
            binaryRepresentation = hexIdStr.replaceAll("-", "");
        }
//        if(null!=binaryRepresentation)
//        return "0x"+binaryRepresentation;
        return binaryRepresentation == null ? null : "0x" + ((String) binaryRepresentation).toUpperCase();
    }

    public static boolean isDefined(final Object value) {
        String value1="";
        if(null!=value)
            value1=value.toString();
        return ((value1 != null) && (value1.length() > 0) && (!value1.equalsIgnoreCase("null")) && (!value1.equalsIgnoreCase("false")));
    }

    /**
     * Method to set tapresponse with count of records
     *
     * @param finalResponse
     * @param count
     * @return
     */
    public static FinalResponse setCount(FinalResponse finalResponse, Long count) {
        if (null != count) {
            finalResponse.setCount(count.intValue()); // As currently client code is fetching from this field , so converting long to int
            // In future if count is not comming proper than add an variable in tapResponse which is of long type
        }
        return finalResponse;
    }

    public static Integer convertStringToInteger(String value) {
        try {
            if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
                return null;
            }
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null; // or throw a custom exception if needed
        }
    }


    public static String generateNodeId() {
        int randomNum = ThreadLocalRandom.current().nextInt(10000000, 99999999);
        return "NODE" + randomNum;
    }


    public static String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

}
