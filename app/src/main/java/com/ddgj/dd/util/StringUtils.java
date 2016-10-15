package com.ddgj.dd.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/8.
 */
public class StringUtils {
    /**根据出生年月获取年龄*/
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
}
