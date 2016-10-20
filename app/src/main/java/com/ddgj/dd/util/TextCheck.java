package com.ddgj.dd.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/20.
 */

public class TextCheck {
    public static boolean checkEmail(String email){
        Pattern p = Pattern.compile("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?");
        Matcher m = p.matcher(email);
        return m.matches();
    }
    public static boolean checkPhoneNumber(String number){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }
}
