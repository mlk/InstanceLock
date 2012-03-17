package com.github.mlk.instancelock;

import java.io.*;

/** Basic file operations. I am not using commons IO or the like as
 * I don't want to have any dependencies for using this API. */
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

    /** Atomic write to a file. In order to do this it first writes the content to a temp file then moves.
     *
     * @param file The destination file.
     * @param content The content to write
     * @throws IOException Any exceptions while writing the file.
     */
    static void writeFile(File file, String content) throws IOException {
        File tmpFile = new File(file.getParent(), file.getName() + ".tmp");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
        try {
            writer.write(content);
            writer.flush();
        } finally {
            writer.close();
        }

        if (!tmpFile.renameTo(file)) {
            throw new IOException("Failed to rename file: " + tmpFile.getName() + " to " + file.getName());
        }
    }
}
