package com.example.dentalhub;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    public static boolean compareDate() throws ParseException {
       String now = "2019-07-17 18:21:26";
       String past = "2019-07-17 12:21:26";
       Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(now);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(past);
        long difference = date1.getTime()-date2.getTime();
        Log.d("DateTimeHelper", String.valueOf(difference));
        return true;
    }
}
