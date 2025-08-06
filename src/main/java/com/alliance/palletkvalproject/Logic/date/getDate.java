package com.alliance.palletkvalproject.Logic.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class getDate {
    public static String getTodayDate(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        return sdf.format(today);
    }
}
