/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.adapter.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rahman.muhammad
 *
 */
public class PowerMonitoringUtility {

    private static final Logger logger = LoggerFactory.getLogger(PowerMonitoringUtility.class.getName());


    public static String getFormattedStartDate(String creationTimeStamp, String duration) {
        StringTokenizer stringTokenizer = new StringTokenizer(creationTimeStamp, ".");
        StringTokenizer stringTokenizer2 = new StringTokenizer(duration, ".");

        // This is old code , we will not change it unless need to change it.
        try {
            while (stringTokenizer.hasMoreElements()) {
                String date1 = stringTokenizer.nextToken();
                String date2 = null;
                int noOfDays = 0, noOfHours = 0, noOfMinutes = 0, noOfSeconds = 0;
                Date formattedDuration;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date formattedDateString = format.parse(date1);
                Calendar cal = Calendar.getInstance();
                cal.setTime(formattedDateString);
                while (stringTokenizer2.hasMoreElements()) {
                    date2 = stringTokenizer2.nextToken();
                    noOfDays = Integer.parseInt(date2.substring(0, 8));
                    noOfHours = Integer.parseInt(date2.substring(8, 10));
                    noOfMinutes = Integer.parseInt(date2.substring(10, 12));
                    noOfSeconds = Integer.parseInt(date2.substring(12, 14));
                    break;
                }
                cal.add(Calendar.DATE, -noOfDays);
                cal.add(Calendar.HOUR, -noOfHours);
                cal.add(Calendar.MINUTE, -noOfMinutes);
                cal.add(Calendar.SECOND, -noOfSeconds);
                formattedDuration = cal.getTime();
                return formattedDuration.toString();
            }
        } catch (Exception exp) {
            logger.error(exp.getMessage(), exp);
        }

        return null;

    }


    public static String getFormattedString(String creationTimeStamp) {
        if (creationTimeStamp == null) {
            return null;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(creationTimeStamp, ".");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date1;
        while (stringTokenizer.hasMoreElements()) {
            date1 = stringTokenizer.nextToken();
            try {
                Date formattedDate = format.parse(date1);
                return formattedDate.toString();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        return creationTimeStamp;
    }

}
