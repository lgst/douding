package com.ddgj.dd.util;

import com.ddgj.dd.util.user.UserHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2016/10/8.
 */
public class StringUtils {
    /**
     * 根据出生年月获取年龄
     */
    public static int getAge(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year1 = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date(currentTimeMillis()));
            int year2 = calendar.get(Calendar.YEAR);
            return year2 - year1;
        } catch (ParseException e) {
            System.out.println("获取年龄失败！");
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDate(String dateStr) {
        long s = 1000;
        long m = 60 * s;
        long h = 60 * m;
        long d = 24 * h;
        long M = 31 * d;
        long y = 12 * M;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            Date date = dateFormat.parse(dateStr);
            long time1 = date.getTime();
            long time2 = currentTimeMillis();
            long time3 = time2 - time1;
            if (time3 < m) {
                return "刚刚";
            } else if (time3 < h) {
                return (time3 / m) + "分钟前";
            } else if (time3 < d) {
                return (time3 / h) + "小时前";
//            } else if (time3 < M) {
//                return (time3 / d) + "天前";
//            } else if (time3 < y) {
//                return (time3 / M + 1) + "个月前";
            } else {
                return dateFormat2.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String TimeMillis2Date(long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.SIMPLIFIED_CHINESE);
        Date date = new Date(timeMillis);
        return dateFormat.format(date);
    }

    public static String getTime(String startDate, String days) {
        Long da = Long.parseLong(days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date d = null;
        try {
            d = dateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = (da * (3600000 * 24)) - (System.currentTimeMillis() - d.getTime());
        long day = time / (3600000 * 24);
        long h = (time % (3600000 * 24)) / 3600000;
        long m = time % 36000000 / 600000;
        if (day<0||h<0||m<0)
            return "任务已结束";
        return "还剩" + day + "天" + h + "小时" + m + "分钟";
    }

    public static String getEndTime(String startTime, String days) {
        long day = Long.parseLong(days) * (24 * 1000*60*60);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.SIMPLIFIED_CHINESE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date d1 = null;
        try {
            d1 = dateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = new Date(d1.getTime() + day);
        return dateFormat.format(d2);
    }


    public static String getSize(long size) {
        if (size < 1024)
            return size + "B";
        else if (size < 1024 * 1024)
            return size / 1024 + "KB";
        else
            return size / 1024 / 1024 + "MB";
    }

    public static String getDbName(){
        return UserHelper.getInstance().getUser().getAccount()+".db";
    }
}
