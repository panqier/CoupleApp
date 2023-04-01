package com.example.couple.util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil implements Serializable {

    public static int differenceInDates(String date1, String date2) {
        if(date1 == null || date2 == null){
            return 0;
        }

        Date formattedDate1;
        Date formattedDate2;

        try{
            formattedDate1 = getRequestDateFormat().parse(date1);
            formattedDate2 = getRequestDateFormat().parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar day1 = Calendar.getInstance();
        Calendar day2 = Calendar.getInstance();
        day1.setTime(formattedDate1);
        day2.setTime(formattedDate2);
        int positiveDifference = daysBetween(day1, day2);
        if(day1.compareTo(day2) < 0) {
            return positiveDifference;
        } else {
            return -positiveDifference;
        }
    }


    /*
    *@return absolute value of difference in days
     */
    public static int daysBetween(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if(dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    private static SimpleDateFormat getRequestDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
}
