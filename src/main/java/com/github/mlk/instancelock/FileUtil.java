package com.github.mlk.instancelock;

import java.io.*;

/** I want to make sure this API as light as I can, so no dependencies. :sigh: */
class FileUtil {
    static String readFully(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            StringBuilder builder = new StringBuilder();
            String str = reader.readLine();
            while(str != null) {
                builder.append(str);
                str = reader.readLine();
            }
            return builder.toString();
        } finally {
            reader.close();
        }
    }
    
    static void writeFile(File file, String content) throws IOException {
        File tmpFile = new File(file.getParent(), file.getName() + ".tmp");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
        try {
            writer.write(content);
            writer.flush();
        } finally {
            writer.close();
        }
        tmpFile.renameTo(file);
    }
}
