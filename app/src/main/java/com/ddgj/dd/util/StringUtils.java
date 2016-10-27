package com.ddgj.dd.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            calendar.setTime(new Date(System.currentTimeMillis()));
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);
            long time1 = date.getTime();
            long time2 = System.currentTimeMillis();
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


    public static String getSize(long size) {
        if (size < 1024)
            return size + "B";
        else if (size < 1024 * 1024)
            return size / 1024 + "KB";
        else
            return size / 1024 / 1024 + "MB";
    }
}