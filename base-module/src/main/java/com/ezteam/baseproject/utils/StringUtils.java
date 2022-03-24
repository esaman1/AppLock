package com.ezteam.baseproject.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String removeAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace("Đ", "D").replace("đ", "d").replaceAll("[!@#$%^&*(),\\-+=]", "");
    }
}
