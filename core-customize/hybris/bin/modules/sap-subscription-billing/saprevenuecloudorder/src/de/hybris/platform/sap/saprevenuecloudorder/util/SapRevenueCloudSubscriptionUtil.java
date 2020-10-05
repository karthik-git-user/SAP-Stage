/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SapRevenueCloudSubscriptionUtil {

    private static final Logger LOG = Logger.getLogger(SapRevenueCloudSubscriptionUtil.class);
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static DateFormat utcDateTimeFormat = null;
    private static DateFormat dateFormat = null;

	private SapRevenueCloudSubscriptionUtil() {
		throw new IllegalStateException("Cannot instantiate utility class");
	}
	
    public static Date formatDate(final String stringDate)
    {
        try {
            if (stringDate != null) 
            {
				final DateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.ENGLISH);
                return format.parse(stringDate);
            } else 
            {
                final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
                return format.parse(LocalDate.now().toString());
            }
        } 
        catch (final ParseException e) 
        {
            LOG.error(e.getMessage());
        }
        return null;
    }

    /**
     * This method is slower as compared to {@link #stringToDate(String)} but supports flexible date format
     * @param strDate string to convert
     * @param strDateFormat format of date
     * @return parsed Date
     */
    public static Date stringToDate(final String strDate, String strDateFormat) {

	    try {
            if (strDate != null) {
                final DateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return dateFormat.parse(strDate);
            }
        }
	    catch (ParseException ex){
            LOG.error("Invalid Date format. Expected format is: "+strDateFormat);
	        LOG.error(ex.getMessage());
        }
        return null;
    }


    /**
     * This method helps in converting a ISO 8601 string to date. using a singleton formatter instance
     * @param strDate date in ISO 8601 Format
     * @return Date object of string. Returns null input is null or input is not in ISO 8601 format
     */
    public static Date stringToDate(final String strDate)
    {
        if(strDate!=null) {

            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.ENGLISH);
            }

            try {
                return dateFormat.parse(strDate);
            }
            catch (ParseException ex){
                LOG.error("Error while parsing date expected format is "+DATE_PATTERN);
            }
        }
        return null;
    }
    
    public static String dateToString(final Date currentDate)
    {
    	if(currentDate!=null)
		{
		    final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
		    return format.format(currentDate);
		}
    	return null;
    }

    public static String dateToString(final Date date, String format){
        if(date != null){
            final DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
            return dateFormat.format(date);
        }
        return null;
    }

    public static String dateToUtcString(final Date date){
	    if(date==null) {
            return null;
        }

	    if(utcDateTimeFormat==null){
            utcDateTimeFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        }

        return utcDateTimeFormat.format(date);
    }

    public static String commerceToSbSortFormat(String sort, Map<String, String> sortFieldMap){
        if(StringUtils.isBlank(sort)){
            return null;
        }

        String[] sortPair = sort.split(",");
        String mappedField = sortFieldMap.getOrDefault(sortPair[0], sortPair[0]);
        return String.format("%s,%s", mappedField, sortPair[1]);
    }
    
}
