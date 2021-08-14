package com.nova.novaidea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@SpringBootApplication
public class NovaIdeaApplication {

    public static String localString2StringUTC(String str) {
        SimpleDateFormat utcFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//转化后UTC时间格式
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localFormater = (SimpleDateFormat) DateFormat.getDateTimeInstance();//解决Date.toLocaleString()过时
        Date date=null;
        try {
            date=localFormater.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utcFormat.format(date);
    }
    public static String getTimesmorning(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().toLocaleString();
    }

    // 获得本月最后一天24点时间
    public static String getTimesMonthnight(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime().toLocaleString();
    }


    public static String getCurrentYearStartTime(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR),0, 1, 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime().toLocaleString();
    }

    public static void main(String[] args) {
        SpringApplication.run(NovaIdeaApplication.class, args);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date =sdf.parse("2021-07-14 14:37:20.323");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            System.out.println("demo  "+getCurrentYearStartTime(calendar));
        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
        }

        try {
            //转换为UTC时间
            System.out.println(localString2StringUTC("2021-08-14 14:37:20.323"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
