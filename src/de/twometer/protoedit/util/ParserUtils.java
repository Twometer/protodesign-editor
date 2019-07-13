package de.twometer.protoedit.util;

import java.util.ArrayList;
import java.util.List;

public class ParserUtils {

    public static String anyStartsAt(char[] a, List<String> target, int idx) {
        List<String> matches = new ArrayList<>();
        for (String str : target)
            if (startsAt(a, str, idx)) {
                matches.add(str);
            }
        if (matches.size() == 0) return null;
        String longest = matches.get(0);
        for (String str : matches) {
            if (str.length() > longest.length())
                longest = str;
        }
        return longest;
    }

    public static boolean endsAt(char[] a, String target, int idx) {
        char[] b = target.toCharArray();
        int j = target.length() - 1;
        for (int i = idx; i >= 0; i--) {
            if (j < 0) break;
            if (a[i] != b[j])
                return false;
            j--;
        }
        return true;
    }

    public static boolean startsAt(char[] a, String target, int idx) {
        char[] b = target.toCharArray();
        int j = 0;
        for (int i = idx; i < a.length; i++) {
            if (j >= b.length) break;
            if (a[i] != b[j])
                return false;
            j++;
        }
        return true;
    }

    public static String buildHtmlTag(String clazz, boolean open) {
        return open ? String.format("<span class=\"%s\">", clazz) : "</span>";
    }

}
