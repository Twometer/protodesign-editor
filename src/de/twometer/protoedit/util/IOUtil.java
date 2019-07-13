package de.twometer.protoedit.util;

import java.io.*;

public class IOUtil {

    public static String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append("\n");

            reader.close();

            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(File file, String contents) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file, false));
            writer.print(contents);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
