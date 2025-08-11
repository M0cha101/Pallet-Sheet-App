package com.alliance.palletkvalproject.Logic.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {
    public static String getTodayDate(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        try{
            return sdf.format(today);
        }catch(Exception e){
            return null;
        }
    }
}
