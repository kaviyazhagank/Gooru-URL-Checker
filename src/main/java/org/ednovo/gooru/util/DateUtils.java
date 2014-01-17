///////////////////////////////////////////////////////////////////////////////////////////////
 // DateUtils.java
 // Gooru-URL Checker
 // Created by Gooru on 2014
 // Copyright (c) 2014 Gooru. All rights reserved.
 // http://www.goorulearning.org/
 // Permission is hereby granted, free of charge, to any person      obtaining
 // a copy of this software and associated documentation files (the
 // "Software"), to deal in the Software without restriction, including
 // without limitation the rights to use, copy, modify, merge, publish,
 // distribute, sublicense, and/or sell copies of the Software, and to
 // permit persons to whom the Software is furnished to do so,  subject to
 // the following conditions:
 // The above copyright notice and this permission notice shall be
 // included in all copies or substantial portions of the Software.
 // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
 // EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
 // MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
 // NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
 // LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 // OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 // WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 //////////////////////////////////////////////////////////////////////////////////////////////
package org.ednovo.gooru.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
	public static final int REPEAT_DAILY = 1;
	public static final int REPEAT_WEEKLY = 2;
	public static final int REPEAT_MONTHLY = 3;
	
	private DateUtils()
	{
		// To ensure that no one accidentally creates an instance of this class
		// Also to prevent extending this class
	}

	public static Date getDate(int year, int month, int day)
	{
		Calendar cal = Calendar.getInstance();

		cal.set(year, month - 1, day, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	public static Date getCurrentDate()
	{
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	public static Date getCurrentDateTime()
	{
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getDateBeforeGivenDate(Date date, int numDays)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DATE, -numDays);

		return cal.getTime();
	}

	public static Date getDateAfterGivenDate(Date date, int numDays)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DATE, numDays);

		return cal.getTime();
	}

	public static String dateFormat(Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return (date == null) ? null : dateFormat.format(date);
	}

	public static String dateTimeFormat(Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return (date == null) ? null : dateFormat.format(date);
	}

	public static boolean isDateExpired(Date date)
	{
		Date currentDate = getCurrentDate();
		int value = currentDate.compareTo(date);
		if (value > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Find the given day of the week (sunday, monday etc.) that falls before
	 * the given date
	 * 
	 * @param date
	 *            The date before which the date we compute should fall
	 * @param dayOfWeek
	 *            The day of week on which the date we compute should fall (Use
	 *            Calendar.SUNDAY etc.)
	 * @return
	 */
	public static Date getDayBeforeGivenDate(Date date, int dayOfWeek)
	{
		// First find what day of week the given date falls
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		int originalDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int dayDiff = dayOfWeek - originalDayOfWeek;

		if (dayDiff >= 0)
		{
			dayDiff -= 7;
		}

		cal.add(Calendar.DATE, dayDiff);

		return cal.getTime();
	}

	/**
	 * Find the given day of the week (sunday, monday etc.) that falls before
	 * the given date
	 * 
	 * @param date
	 *            The date after which the date we compute should fall
	 * @param dayOfWeek
	 *            The day of week on which the date we compute should fall (Use
	 *            Calendar.SUNDAY etc.)
	 * @return
	 */
	public static Date getDayAfterGivenDate(Date date, int dayOfWeek)
	{
		// First find what day of week the given date falls
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		int originalDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int dayDiff = dayOfWeek - originalDayOfWeek;

		if (dayDiff <= 0)
		{
			dayDiff += 7;
		}

		cal.add(Calendar.DATE, dayDiff);

		return cal.getTime();
	}

	/**
	 * Find the given day of the week (sunday, monday etc.) that falls before
	 * the given date
	 * 
	 * @param date
	 *            The date on or before which the date we compute should fall
	 * @param dayOfWeek
	 *            The day of week on which the date we compute should fall (Use
	 *            Calendar.SUNDAY etc.)
	 * @return
	 */
	public static Date getDayOnOrBeforeGivenDate(Date date, int dayOfWeek)
	{
		// First find what day of week the given date falls
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		int originalDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int dayDiff = dayOfWeek - originalDayOfWeek;

		if (dayDiff > 0)
		{
			dayDiff -= 7;
		}
		cal.add(Calendar.DATE, dayDiff);

		return cal.getTime();
	}

	/**
	 * Find the given day of the week (sunday, monday etc.) that falls before
	 * the given date
	 * 
	 * @param date
	 *            The date on or after which the date we compute should fall
	 * @param dayOfWeek
	 *            The day of week on which the date we compute should fall (Use
	 *            Calendar.SUNDAY etc.)
	 * @return
	 */
	public static Date getDayOnOrAfterGivenDate(Date date, int dayOfWeek)
	{
		// First find what day of week the given date falls
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		int originalDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int dayDiff = dayOfWeek - originalDayOfWeek;

		if (dayDiff < 0)
		{
			dayDiff += 7;
		}

		cal.add(Calendar.DATE, dayDiff);

		return cal.getTime();
	}

	public static boolean isInFuture(Date date)
	{
		Date currentDate = getCurrentDate();
		int value = currentDate.compareTo(date);
		if (value < 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static int getDayDifference(Date startDate, Date endDate)
	{
		Calendar startDateCal = Calendar.getInstance();
		startDateCal.setTime(startDate);
		startDateCal.set(Calendar.HOUR, 0);
		startDateCal.set(Calendar.MINUTE, 0);
		startDateCal.set(Calendar.SECOND, 0);
		startDateCal.set(Calendar.MILLISECOND, 0);

		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTime(endDate);
		endDateCal.set(Calendar.HOUR, 0);
		endDateCal.set(Calendar.MINUTE, 0);
		endDateCal.set(Calendar.SECOND, 0);
		endDateCal.set(Calendar.MILLISECOND, 0);

		return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 86400));
	}
	
	/**
	 * Compare two dates
	 * 
	 * @param startDate
	 * @param endDate
	 * @return 0 - startDate and endDate is equal; 1 - startDate has passed endDate; -1 if startDate has not passed endDate  
	 */
	public static int compareDatesWithoutTime(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			Date dateToCompare1 = null;
			Date dateToCompare2 = null;

			try {
				dateToCompare1 = sdf.parse(sdf.format(date1));
				dateToCompare2 = sdf.parse(sdf.format(date2));
			} catch (ParseException e) {
				dateToCompare1 = date1;
				dateToCompare2 = date2;
			}
			return dateToCompare1.compareTo(dateToCompare2);
		}
		return 0;
	}

	/**
	 * This Methods gives the result Milliseconds elapsed to Current Time
	 * Note : this method does not check for dates. It only works on time
	 * Example : 
	 * Input Time : 12 July 2011 12:20:80 AM
	 * The input will be converted to current date and the time will be retained 
	 * and this newly formed date will be compared with current date's time.  
	 * @param startTime
	 * @return
	 */
	public static long timeDifferenceWithCurrentTime(Date startTime) {
		
		Date currentTime = getCurrentDateTime();
		Calendar startDateCal = Calendar.getInstance();
		startDateCal.setTime(startTime);
		
		Calendar newTime = Calendar.getInstance();
		newTime.set(Calendar.HOUR, startDateCal.get(Calendar.HOUR));
		newTime.set(Calendar.MINUTE, startDateCal.get(Calendar.MINUTE));
		newTime.set(Calendar.MILLISECOND, startDateCal.get(Calendar.MILLISECOND));
		
		return ((newTime.getTime()).getTime() - currentTime.getTime()) ;
	}

	
	
	public static long daysBetween2Dates(Date startDate, Date endDate) {

		Calendar startDateCal = Calendar.getInstance();
		startDateCal.setTime(startDate);
		startDateCal.set(Calendar.HOUR, 0);
		startDateCal.set(Calendar.MINUTE, 0);
		startDateCal.set(Calendar.SECOND, 0);
		startDateCal.set(Calendar.MILLISECOND, 0);

		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTime(endDate);
		endDateCal.set(Calendar.HOUR, 0);
		endDateCal.set(Calendar.MINUTE, 0);
		endDateCal.set(Calendar.SECOND, 0);
		endDateCal.set(Calendar.MILLISECOND, 0);
		
		
		return (startDateCal.getTime().getTime() - endDateCal.getTime()
				.getTime())	/ (24 * 3600 * 1000);
	}

	public static int decodeTime(String s) throws Exception {
		SimpleDateFormat f = new SimpleDateFormat("hh:mm aaa");
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		f.setTimeZone(utcTimeZone);
		f.setLenient(false);
		ParsePosition p = new ParsePosition(0);
		Date d = f.parse(s, p);
		if (d == null || !isRestOfStringBlank(s, p.getIndex()))
			throw new Exception("Invalid time value (hh:mm:ss): \"" + s + "\".");
		return (int) d.getTime();
	}
	
	// Returns true if string s is blank from position p to the end.
	public static boolean isRestOfStringBlank(String s, int p) {
		while (p < s.length() && Character.isWhitespace(s.charAt(p)))
			p++;
		return p >= s.length();
	}
	
	public static Date stringDateToDate(final String strDate)
	
	{
	    Date dateToReturn = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    try
	    {
	        dateToReturn = (Date)dateFormat.parse(strDate);
	    }
	    catch (ParseException e)
	    {
	        e.printStackTrace();
	    }

	    return dateToReturn;
	}
	
}
