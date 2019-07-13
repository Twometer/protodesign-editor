package de.twometer.protoedit.util;

import static de.twometer.protoedit.util.ParserUtils.startsAt;

public class CodeScanner {

    public static String scan(String html, String languageCode, Callback callback) {
        char[] data = html.toCharArray();
        boolean inCodeBlock = false;
        int inCodeBlockNext = -1;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String key = "<pre><code class=\"language-" + languageCode + "\">";
            if (startsAt(data, key, i)) inCodeBlockNext = i + key.length() - 1;
            else if (startsAt(data, "</code></pre>", i)) inCodeBlock = false;
            if (inCodeBlockNext == i) {
                inCodeBlock = true;
                inCodeBlockNext = -1;
            }
            if (inCodeBlock) {
                callback.onIteration(data, i, output);
            } else output.append(data[i]);
        }
        return output.toString();
    }

    public interface Callback {
        void onIteration(char[] data, int i, StringBuilder output);
    }

}
