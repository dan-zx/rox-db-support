package com.grayfox.server.test.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class Utils {

    private Utils() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    public static String getContentFromFileInClasspath(String file) {
        try {
            StringBuilder content = new StringBuilder();
            for (String line : Files.readAllLines(Paths.get(Utils.class.getClassLoader().getResource(file).toURI()))) content.append(line).append('\n');
            return content.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}