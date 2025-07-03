package com.example.recommend_lunch_menu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegEx {
    public static boolean isRegExEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}
