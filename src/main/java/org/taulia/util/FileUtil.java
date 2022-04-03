package org.taulia.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void write(String filePath, String content) throws IOException {

        File file = new File(filePath);

        boolean isExists = file.exists();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        if (isExists) {
            writer.newLine();
        }
        writer.append(content);
        writer.flush();
        writer.close();
    }
}
