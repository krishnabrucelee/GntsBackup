/**
 * 
 */
package com.lyca.api.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Krishna
 *
 */
public class DateConvertUtil {

	public static String getDateDiff(String dateStart, String dateEnd) {
		
		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date d1 = null;
		Date d2 = null;

		String date = null;
		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateEnd);
			long diff;
			//in milliseconds
			long diffDate = d1.getTime() - d2.getTime();

			if (diffDate > 0) {
				diff = diffDate;
			} else {
				diff = d2.getTime() - d1.getTime();
			}
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");

			if (diffDays < 1 && diffHours < 1 &&  diffMinutes < 1) {
				date = diffSeconds + "s";
				return date;
			} else if (diffDays < 1 && diffHours < 1) {
				date = diffMinutes + "m " + diffSeconds + "s";
				return date;
			} else if (diffDays < 1) {
				date = diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
				return date;
			} else {
				date = diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
				return date;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
}
